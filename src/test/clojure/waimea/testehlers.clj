(ns waimea.testehlers
    (:require [waimea.utils :as C])
    (:require [waimea.filters.ehlers.itrend :as I])
    (:require [waimea.filters.ehlers.cc :as CC])
    (:use clojure.test))

(let [a (range 100)
      itr (I/calc-itrend a 20)]
      (is (= (count a) (count itr))))





