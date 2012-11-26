(ns waimea.filters.ehlers.itrend
    (:require [waimea.utils.commonutils :as C]))

(defn init-itrend [data counter result]
    (when (> counter 0)
        (let [[a b c] (take 3 data) 
              cur-val (* 0.25 (+ a (* 2.0 b) c))]
            (reset! result (conj @result cur-val)))
        (recur (rest data) (dec counter) result)))

(defn calc-itrendx [alpha data result]
    (let [
            aa (* alpha alpha)
            f1 (- alpha (* 0.25 aa))
            f2 (* aa 0.5)
            f3 (- alpha (* 0.75 aa))
            f4 (- 1.0 alpha)
            f5 (* f4 f4)
          ]
        (loop [v (-> data rest rest rest rest rest rest) 
               v-1 (nth @result 7) 
               v-2 (nth @result 6)]
            ;(when (> (count v) 2) <== makes it dog slow!
            (let [[a b c] (take 3 v)]
                (if (not (nil? c)) 
                    (let [
                            pos-val (+ (* f1 c) (* f2 b) (* 2.0 f4 v-1))
                            neg-val (+ (* f3 a) (* f5 v-2)) 
                            res-val (- pos-val neg-val)
                        ]
                        (reset! result (conj @result res-val))
                        (recur (rest v) res-val v-1)))))))

(defn calc-itrend [data, ^Integer days]
    {:pre [(>= (count data) 20)]
     :post [= (count %) (count data)]}
    (let [result (atom [])
          alpha  (C/calc-alpha days)]
        (init-itrend data 8 result)
        (calc-itrendx alpha data result)
        @result))

