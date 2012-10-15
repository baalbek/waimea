(ns waimea.filters.filterutils)

(defn calc-weighted-value [values weights]
  (let [m (map #(* % %2) values weights)
        mx (reduce + weights)]
    (/ (reduce + m) mx)))

(defn weighted-values [values weights window]
  (let [ cut-off (- window 1)
         weights-p (partition window 1 weights)
         values-p (partition window 1 values)]
    (concat
      (take cut-off values)
      (map #(calc-weighted-value % %2) values-p weights-p))))