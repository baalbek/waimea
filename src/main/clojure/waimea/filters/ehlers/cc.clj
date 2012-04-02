(ns waimea.filters.ehlers.cc
    (:require [waimea.filters.ehlers.common :as C]))


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
          alpha (C/calc-alpha days)
          smooth (C/calc-smooth data)
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
          result (loop [cyclex cycle, datax (drop 18 smooth), ax a, bx b]
            (let [[c d e] (take 3 datax)]
                (if (nil? e)
                    cyclex
                    (let [cycle-val (calc-cycle ax bx c d e)]
                        (recur (conj cyclex cycle-val) (rest datax) bx cycle-val)))))
            ]
            result))

                      

