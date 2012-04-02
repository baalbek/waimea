(ns waimea.filters.ehlers.common
    (:import (java.util Date GregorianCalendar Calendar))
    (:use [clojure.contrib.str-utils :only (re-split)]))

(def cal0 (GregorianCalendar.))
(def cal1 (GregorianCalendar.))
(def MILLIS_IN_DAY (* 60 60 24 1000))

(defn diff-days [d0 d1]
    (.setTime cal0 d0)
    (.setTime cal1 d1)
    (let
        [mill0 (.getTimeInMillis cal0)
         mill1 (.getTimeInMillis cal1)
         diff (- mill1 mill0)]
         (/ diff MILLIS_IN_DAY)))

(defn new-date [y m d]
    (Date. (- y 1900) (- m 1) d))

(defn date-add-days [dx days]
    (.setTime cal0 dx)
    (.add cal0 Calendar/DAY_OF_YEAR days)
    (.getTime cal0))


(defn parse-date [s]
    (let [[y m d] (map #(Integer. %) (re-split #"-" s))] 
        (new-date y m d)))

(defn calc-smooth [data]
    (loop [result (vec (take 4 data))
           datax (drop 1 data)]
        (let [[a b c d] (take 4 datax)]
            (if (nil? d)
                result
                (recur (conj result (/ (+ a (* 2.0 b) (* 2.0 c) d) 6.0)) (rest datax))))))

(defn calc-alpha [days]
    (/ 2.0 (+ days 1.0)))

(defn calc-days [alpha]
    (- (/ 2.0 alpha) 1.0))
