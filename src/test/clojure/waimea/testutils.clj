(ns waimea.testutils
  (:require
    [waimea.utils :as U]
    [waimea.filters.filterutils :as FU])
    (:use clojure.test))

(is (= 1 1))

(def a [1.0 2.0 3.0 4.0 5.0 6.0 7.0 8.0 9.0 10.0])
(def b [11.0 12.0 13.0 14.0 15.0 16.0 17.0 18.0 19.0 20.0])

(let [ab (FU/weighted-values a b 3)]
  (is (= (count ab) 8)))
