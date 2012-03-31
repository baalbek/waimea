(ns waimea.filters.ehlers
    (:require [taharoa.models.filters.filterutils :as FU]
              [taharoa.utils.utils :as U]
              [clojure.contrib.math :as M])
    (:import (java.util ArrayList)))

(defn init-itrend [data counter result]
    (when (> counter 0)
        (let [[a b c] (take 3 data) 
              cur-val (* 0.25 (+ a (* 2.0 b) c))]
            (reset! result (conj @result cur-val)))
        (recur (rest data) (dec counter) result)))

(defn calc-itrend [alpha data result]
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
            ;(when (> (count v) 2)
            (let [[a b c] (take 3 v)]
                (if (not (nil? c)) 
                    (let [
                            pos-val (+ (* f1 c) (* f2 b) (* 2.0 f4 v-1))
                            neg-val (+ (* f3 a) (* f5 v-2)) 
                            res-val (- pos-val neg-val)
                        ]
                        (reset! result (conj @result res-val))
                        (recur (rest v) res-val v-1)))))))

(defn itrend [data, ^Integer days]
    (let [result (atom [])
          alpha  (FU/calc-alpha days)]
        (init-itrend data 8 result)
        (calc-itrend alpha data result)
        @result))


(defn itrend-with-dates [data days]
    (let [alpha  (FU/calc-alpha days)
          itrend-calc (itrend alpha (:vals data))]
          (U/zip-vk itrend-calc (:dates data) :value :dx)))


(defn calc-smooth [data]
    (loop [result (vec (take 4 data))
           datax (drop 1 data)]
        (let [[a b c d] (take 4 datax)]
            (if (nil? d)
                result
                (recur (conj result (/ (+ a (* 2.0 b) (* 2.0 c) d) 6.0)) (rest datax))))))


(defn ave [lx, ^Double cc-val, ^Double f1, ^Double f2]
    (let [[a b] (take 2 lx)
           c (* f2 cc-val)
           d (* f1 (- b a))]
        (+ c d)))

(defn calc-cycle-fn [^Double f12, ^Double f2, ^Double f22]
    (fn [^Double a,
         ^Double b, 
         ^Double c, 
         ^Double d, 
         ^Double e]
        (let [data-val (* f12 (- (+ e c) (* 2.0 d)))
              cycle-val (- (* 2.0 f2 b) (* f22 a))]
        (+ data-val cycle-val))))

(defn cybercycle [data, ^Integer days]
    {:pre [(>= (count data) 20)]
     :post [= (count %) (count data)]}
    (let [
          alpha (FU/calc-alpha days)
          smooth (calc-smooth data)
          f1 (- 1.0 (* alpha 0.5))
          f12 (* f1 f1)
          f2 (- 1.0 alpha)
          f22 (* f2 f2)
          cycle (loop [result [0.0] sx (take 20 smooth)]
                    (if (< (count sx) 2)
                        result 
                        (recur (conj result (ave sx (last result) f1 f2)) (rest sx))))
          [a b] (drop (- (count cycle) 2) cycle)
          calc-cycle (calc-cycle-fn f12 f2 f22)
          result (loop [cyclex cycle, datax (drop 18 data), ax a, bx b]
            (let [[c d e] (take 3 datax)]
                (if (nil? e)
                    cyclex
                    (let [cycle-val (calc-cycle ax bx c d e)]
                        (recur (conj cyclex cycle-val) (rest datax) bx cycle-val)))))
          ;;cc-max (max (apply max tmp) (M/abs (apply min tmp)))
          ;;result (map #(/ % cc-max) tmp)  
            ]
            result))

                      
(defn cybercycle-with-dates [data days]
    (let [alpha  (FU/calc-alpha days)
          cc (cybercycle alpha (:vals data))]
          (U/zip-vk cc (:dates data) :value :dx)))


        
