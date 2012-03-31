(ns waimea.testehlers
    (:require [waimea.filters.ehlers.common :as C])
    (:use clojure.test))

;;(is (= (Swoosh/jada 2 2) 4))

(let [a (range 10)
      result (C/calc-smooth a)]
      (println (class result)))

(println "Hi")
    
