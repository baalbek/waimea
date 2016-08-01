(ns maunaloa.views.viewscommon
  (:import
    [java.util Collections]
    [java.sql Date]
    [java.time LocalDate DayOfWeek]
    [oahux.chart MaunaloaChart]
    [oahux.repository ColorRepository ColorReposEnum]
    [oahux.controllers MaunaloaChartViewModel]
    [javafx.scene.paint Color]
    [javafx.scene.canvas Canvas GraphicsContext]
    [vega.filters.ehlers Itrend CyberCycle])
  (:require
    [maunaloa.models.candlestickmodel :as CM]
    [maunaloa.utils.commonutils :as U]
    (waimea.blocks
      [block :as B]
      [quadrant :as Q])))

(comment colors
  {
    :itrend-200 Color/BLUE
    :itrend-50 Color/RED
    :itrend-10 Color/DARKMAGENTA
    :stockprice Color/BLACK
    :volume Color/RED
  })



(comment
  ([category]
    (let [kw (keyword category)]
      (kw colors)))
  ([category sub-cat]
    (let [kw (keyword (str category "-" sub-cat))]
      (kw colors))))

(def line-widths
  {
    :itrend-200 1.0
    :itrend-50 1.0
    :itrend-10 0.5
    :stockprice 0.5
    :volume 0.5
  })

(defn get-lw
  ([category]
    (let [kw (keyword category)]
      (kw line-widths)))
  ([category sub-cat]
    (let [kw (keyword (str category "-" sub-cat))]
      (kw line-widths))))

(def calc-itrend (Itrend.))

(def calc-cc (CyberCycle.))

(defn create-freqs [f data-values freqs]
  (map
    #(f data-values %) freqs))


;;------------------------------------------------------------------------
;;----------------------------- Chart Draw -------------------------------
;;------------------------------------------------------------------------
;(defn draw-canvas [canvas view-model q-blox fg-color bg-color]
(defn draw-canvas [this canvas view-model q-blox]
  (let [^ColorRepository color-repos (.getColorRepos this)
        bg-color (.colorFor color-repos ColorReposEnum/BACKGROUND)
        fg-color (.colorFor color-repos ColorReposEnum/FOREGROUND)
        w (.getWidth ^Canvas canvas)
        h (.getHeight ^Canvas canvas)
        gc ^GraphicsContext (.getGraphicsContext2D canvas)
        mleft 60
        mtop 10
        mright 10
        mbtm 30
        ]
    (doto gc
      (.setFill bg-color)
      (.fillRect 0 0 w h)
      (.setStroke fg-color)
      (.strokeRect (+ mleft 0.5) (+ mtop 0.5)
        (- w (+ mleft mright))
        (- h (+ mtop mbtm))))
    (let [qsx (B/block-chain
                :qs q-blox 
                :h (- h mtop mbtm)
                :x0 mleft
                :x1 (- w mright)
                :y0 mtop)]
      (let [[hr vr] (Q/plot-quadrant gc (first qsx))]
        (.setHruler view-model hr)
        (.setVruler view-model vr)
        )
      (doseq [q (rest qsx)]
        (Q/plot-quadrant gc q)))))

(defn create-bundlex [beans freqs]
  (let [prices (U/j1 .getValue beans)
        result
        {:size (count beans)
         :beans (vec beans)
         :prices (vec prices)
         ;:dx (vec (map #(-> % .getDx .toLocalDate) beans))
         :dx (vec (map #(.getDx %) beans))
         :freqs freqs
         :itrends (map vec (create-freqs calc-itrend prices freqs))
         :cc (map vec (create-freqs calc-cc prices freqs))
         :volume (vec (U/j1 .getVolume beans))}]
    result))

(defn create-bundle [this ticker]
  (let [cache-ref (.state this)
        vm ^MaunaloaChartViewModel (:viewmodel @cache-ref)
        is-weekly (:isweekly @cache-ref)
        beans (if (= is-weekly true)
                (CM/candlestick-weeks (.stockPrices vm 1))
                (.stockPrices vm 1))
        freqs (if (= is-weekly true)
                [10 50]
                [10 50 200])
        result (create-bundlex beans freqs)]
    (println "Creating bundle for " ticker)
    (swap! cache-ref assoc ticker result)
    result))

(defn get-bundle [this]
  (let [cache-ref (.state this)
        vm ^MaunaloaChartViewModel (:viewmodel @cache-ref)
        is-weekly (:isweekly @cache-ref)
        tickerx (-> vm .getStock .getTicker)
        ticker (if (= is-weekly true) (str tickerx "W") tickerx)
        bundlex (@cache-ref ticker)
        result (if (nil? bundlex)
                 (create-bundle this ticker)
                  bundlex)]
    result))

(defn shift [this create-blox-fn canvas skip num-items]
  (let [__state__ @(.state this)
        [vm blox] (create-blox-fn this num-items (max 0 skip))]
    (println "skip: " skip)
    (if (< skip 0)
      (reset! (:skip __state__) 0))
    (draw-canvas this canvas vm blox)))

(defn shiftDays [create-blox-fn num-items]
  (fn [^MaunaloaChart this
       ^Integer days
       ^Canvas c]
    (let [__state__ @(.state this)
          wx (if (= (:isweekly __state__) true) (int (/ days 5)) days)
          cur-skip (swap! (:skip __state__) + wx)]
      (shift this create-blox-fn c cur-skip num-items))))


(defn shiftWeeks [create-blox-fn num-items]
  (fn [^MaunaloaChart this
       ^Integer weeks
       ^Canvas c]
    (let [__state__ @(.state this)
          wx (if (= (:isweekly __state__) true) weeks (* 5 weeks))
           cur-skip (swap! (:skip __state__) + wx)]
      (shift this create-blox-fn c cur-skip num-items))))

(defn draw [create-blox-fn num-items]
  (fn [^MaunaloaChart this
       ^Canvas c]
    (let [__state__ @(.state this)
          cur-skip @(:skip __state__)
          [vm blox] (create-blox-fn this num-items cur-skip)]
      (draw-canvas this c vm blox))))

(defn shiftToEnd [create-blox-fn num-items]
  (fn [^MaunaloaChart this
       ^Canvas c]
    (let [__state__ @(.state this)
          [vm blox] (create-blox-fn this num-items 0)]
      (reset! (:skip __state__) 0)
      (draw-canvas this c vm blox))))

(defn binary-search-dx [dxx loc-d]
  (let [result (Collections/binarySearch dxx loc-d compare)]
    (println "Checking for " loc-d)
    (if (< result 0)
      nil
      result)))


(defn find-index [dxx date is-weekly]
  (let [find-fn (partial binary-search-dx dxx)
        search-dates
        (if (= is-weekly false)
          (map #(Date/valueOf (.minusDays date %)) (range 5))
          (let [week-date (.adjustInto DayOfWeek/FRIDAY date)]
            (map #(Date/valueOf (.minusWeeks week-date %)) (range 5))))]
    (loop [sds search-dates
            result nil]
      (if (or (nil? sds) (not= nil result))
        result
        (recur (next sds) (find-fn (first sds)))))))

(defn shiftToDate [create-blox-fn num-items]
  (fn [^MaunaloaChart this
       ^LocalDate date
       ^Canvas c]
    (let [__state__ @(.state this)
          bundle (get-bundle this)
          is-weekly (:isweekly __state__)
          index (find-index (:dx bundle) date is-weekly)]
      (if-not (nil? index)
        (let [new-skip (- (:size bundle) index 1)
              [vm blox] (create-blox-fn this num-items new-skip)]
          (println "Date: " date "Index: " index)
          (reset! (:skip __state__) new-skip)
          (draw-canvas this c vm blox))))))


