(ns maunaloa.utils.commonutils
  (:import
    [java.util Date]
    [oahu.financial StockPrice])
  (:require
    [clojure.string :as cs]))

(defn new-date [y m d]
  (Date. (- y 1900) (- m 1) d))

;;------------------------------------------------------------------------
;;------------------------------- Macros ---------------------------------
;;------------------------------------------------------------------------
(defmacro vec-map-beans [map-fn beans]
  `(vec (map #(~map-fn ^StockPrice %) ~beans)))

(defmacro j1 [f1 items] `(map (fn [v#] (~f1 v#)) ~items))

;(defmacro j2 [f1 f2] `(fn [v#] [(~f1 v#) (~f2 v#)]))

;(defmacro j3 [f1 f2 f3] `(fn [v#] [(~f1 v#) (~f2 v#) (~f3 v#)]))

(defn as-get-set [^String p get-or-set prefix]
  (str prefix get-or-set (cs/upper-case (first p)) (.substring p 1)))

(defn p2kw [prop]
  (keyword (cs/lower-case prop)))

(defn getter [prop get-fn default]
  (if (nil? default)
    `(def ~get-fn
       (fn [this#]
         (let [cache# (.state this#)]
           (~prop @cache#))))
    `(def ~get-fn
       (fn [this#]
         (let [cache# (.state this#)
               val# (~prop @cache#)]
           (if (nil? val#)
             (do
               (swap! cache# assoc ~prop ~default)
               ~default)
             val#))))))

(defn setter [prop set-fn]
  `(def ~set-fn
     (fn [this# value#]
       (let [cache# (.state this#)]
         (swap! cache# assoc ~prop value#)))))

(defn getsetter [prop get-fn set-fn default]
  (if (nil? default)
    `(do
      (def ~set-fn
        (fn [this# value#]
          (let [cache# (.state this#)]
            (swap! cache# assoc ~prop value#))))
      (def ~get-fn
          (fn [this#]
            (let [cache# (.state this#)]
              (~prop @cache#)))))
    `(do
      (def ~set-fn
        (fn [this# value#]
          (let [cache# (.state this#)]
            (swap! cache# assoc ~prop value#))))
      (def ~get-fn
          (fn [this#]
            (let [cache# (.state this#)
                  val# (~prop @cache#)]
              (if (nil? val#)
                (do
                  (swap! cache# assoc ~prop ~default)
                  ~default)
                val#)))))))


(defmacro defprop [variants prop &
                   { :keys [prefix default]
                     :or {prefix "-"
                          default nil}}]
  (let [s-prop (p2kw prop)
        get-fn (symbol (as-get-set prop "get" prefix))
        set-fn (symbol (as-get-set prop "set" prefix))]
    (cond
      (= variants :getset)
        (getsetter s-prop get-fn set-fn default)
      (= variants :get)
        (getter s-prop get-fn default)
      (= variants :set)
        (setter s-prop set-fn))))




