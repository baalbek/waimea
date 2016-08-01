(ns vega.financial.calculator.BlackScholes
  ( :gen-class
    :implements [oahu.financial.OptionCalculator])
  (:import
    [cern.jet.random Normal]
    [cern.jet.random.engine MersenneTwister]
    [oahu.financial Derivative DerivativePrice StockPrice]
    [oahu.exceptions BinarySearchException]))

(def norm (Normal. 0.0 1.0 (MersenneTwister.)))

(def interest-rate 0.05)


(defn calc-D2 [^double d1
               ^double t
               ^double sigma]
  (- d1
    (* sigma
      (Math/sqrt t))))

(defn calc-D1 [^double spot
              ^double x
              ^double t
              ^double sigma]
  (let [
        a (Math/log (/ spot x))
        b (* t
            (+ interest-rate
              (* sigma (/ sigma 2.0))))
        c (* sigma (Math/sqrt t))]
    (/ (+ a b) c)))


;  (S * norm.cdf(d1)) - (X * Math.exp(-r * T) * norm.cdf(d2))
;  (X * Math.exp(-r * T) * norm.cdf(-d2)) - (S * norm.cdf(-d1))
(defn call-price [^double spot
                  ^double x
                  ^double t
                  ^double sigma]
  (let [
        d1 (calc-D1 spot x t sigma)
        d2 (calc-D2 d1 t sigma)
        cdf1 (.cdf ^Normal norm d1)
        cdf2 (.cdf ^Normal norm d2)
        xp (Math/exp
            (* t
              (- interest-rate)))
        a (* spot cdf1)
        b (* x xp cdf2)]
    (- a b)))

(defn put-price [^double spot
                  ^double x
                  ^double t
                  ^double sigma]
  (let [d1 (calc-D1 spot x t sigma)
        d2 (calc-D2 d1 t sigma)
        cdf1 (.cdf ^Normal norm (- d1))
        cdf2 (.cdf ^Normal norm (- d2))
        xp (Math/exp
            (* t
              (- interest-rate)))
        a (* spot cdf1)
        b (* x xp cdf2)]

    (max (- x spot) (- b a))))

(defn find-bounds [f start-val target]
  (let [r (f start-val)
        fcat  (let [r-1 (f (+ start-val 1))]
                (if (> r-1 r) :incr :decr))
        cmp-1 (if (= fcat :incr) < >)
        cmp-2 (if (= fcat :incr) >= <=)]
    (if (cmp-1 r target)
      (loop [s (* start-val 2.0)]
        (let [r2 (f s)]
          (if (cmp-2 r2 target)
            {:start start-val :end s :end-res r2 :fcat fcat}
            (recur (* s 2.0)))))
      {:start 0.0 :end start-val :end-res r :fcat fcat})))

(defn is-within-tolerance [v target tolerance]
  (< (Math/abs (- v target)) tolerance))

(defn binary-search-run [f bounds target tolerance]
  (let [cmp (if (= (:fcat bounds) :incr) < >)
        max-iter 25]
    (loop [lo (:start bounds)
           hi (:end bounds)
           counter 0]
      ;(prn lo hi)
      (if (> counter max-iter)
        (throw (BinarySearchException. (str "Max iterations (" max-iter ") reached"))))
      (let [mid (/ (+ hi lo) 2.0)
            mid-v (f mid)]
        (if (is-within-tolerance mid-v target tolerance)
          mid
          (if (cmp mid-v target)
            (recur mid hi (inc counter))
            (recur lo mid (inc counter))))))))

(defn binary-search [f start-val target tolerance]
  (let [bounds (find-bounds f start-val target)]
    (if (is-within-tolerance (:end-res bounds) target tolerance)
      (:end bounds)
      (binary-search-run f bounds target tolerance))))

(defn price-fn [^DerivativePrice deriv]
  (if (= Derivative/CALL (-> deriv .getDerivative .getOpType))
        call-price
        put-price))


(defn spot-finder [^DerivativePrice d]
  (let [f (price-fn d)
        x (-> d .getDerivative .getX)
        t (/ (.getDays d) 365.0)
        sigma (.getIvBuy d)]
    (fn [spot]
      (f spot x t sigma))))

;;------------------------------------------------------------------------
;;-------------------------- Interface methods ---------------------------
;;------------------------------------------------------------------------

(defn -delta [this, ^DerivativePrice cb]
  (let [
         iv (.getIvSell cb)
         new-spot (+ 1.0 (-> cb .getStockPrice .getValue))
         new-price ((price-fn cb)
                     new-spot
                     (-> cb .getDerivative .getX)
                     (/ (.getDays cb) 365.0)
                     iv)]
    (- new-price (.getSell cb))))


(defn -spread [this, ^DerivativePrice deriv]
  0.0)


(defn -breakEven [this, ^DerivativePrice deriv]
  0.0)



(defn -stockPriceFor [this
                    ^double optionPrice
                    ^DerivativePrice deriv]
  (let [f (spot-finder deriv)
        start-val (-> deriv .getStockPrice .getCls)]
    (binary-search f start-val optionPrice 0.1)))


(defn -iv [this
          ^DerivativePrice cb
          priceType]
  (let [
         price (if (= Derivative/BUY priceType)
                     (.getBuy cb)
                     (.getSell cb))
         opx-f (partial
                 (price-fn cb)
                  (-> cb .getStockPrice .getCls)
                  (-> cb .getDerivative .getX)
                  (/ (.getDays cb) 365.0))
           ]
    (binary-search opx-f 0.4 price 0.01)
  ))

(defn -ivCall [this
               spot
               x
               t
               price]
  (let [opx-f (partial
                call-price spot x t)]
    (binary-search opx-f 0.4 price 0.01)))

(defn -ivPut [this
               spot
               x
               t
               price]
  (let [opx-f (partial
                put-price spot x t)]
    (binary-search opx-f 0.4 price 0.01)))

(defn -callPrice [this spot x t sigma]
  (call-price spot x t sigma))

(defn -putPrice [this spot x t sigma]
  (put-price spot x t sigma))
