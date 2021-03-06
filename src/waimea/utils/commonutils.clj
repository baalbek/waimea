(ns waimea.utils.commonutils
    (:import
      (java.util Date GregorianCalendar Calendar)
      (clojure.lang Seqable))
    (:require
      (clojure.math [numeric-tower :as M]))
    (:use
      [clojure.string :only (split)]))

(def cal0 (GregorianCalendar.))
(def cal1 (GregorianCalendar.))
(def MILLIS_IN_DAY (* 60 60 24 1000))


(defn start-next-month [^Date d]
  (.setTime ^GregorianCalendar cal0 d)
  (.add ^GregorianCalendar cal0 Calendar/MONTH 1)
  (let [^Date cd (.getTime ^GregorianCalendar cal0)]
    (Date. (.getYear cd) (.getMonth cd) 1)))

(defn find-dates-between [start-date end-date interval]
  (cond (= :month interval)
    (loop [^Date nx (start-next-month start-date) result []]
      (if (.after nx end-date)
         result
         (recur (start-next-month nx) (conj result nx))))))

(defn diff-days [d0 d1]
  (.setTime ^GregorianCalendar cal0 d0)
  (.setTime ^GregorianCalendar cal1 d1)
  (let
    [mill0 (.getTimeInMillis ^GregorianCalendar cal0)
     mill1 (.getTimeInMillis ^GregorianCalendar cal1)
     diff (- mill1 mill0)]
     (/ diff MILLIS_IN_DAY)))

(defn new-date [y m d]
  (Date. (- y 1900) (- m 1) d))

(defn date-add-days [dx days]
  (.setTime ^GregorianCalendar cal0 dx)
  (.add ^GregorianCalendar cal0 Calendar/DAY_OF_YEAR days)
  (.getTime ^GregorianCalendar cal0))


(defn parse-date [s]
  (let [[y m d] (map #(Integer. ^String %) (split #"-" s))]
    (new-date y m d)))

(defn calc-days [alpha]
  (- (/ 2.0 alpha) 1.0))

(defn find-in-lists [predicate vex]
  (loop [mv (apply predicate (first vex)) lx (rest vex)]
    (if (= (count lx) 0)
      mv
      (recur (predicate mv (apply predicate (first lx))) (rest lx)))))

(defn find-min-max [vex]
  (let [min-result (find-in-lists min vex)
        max-result (find-in-lists max vex)]
    [min-result max-result]))

(defn norm-v [v]
  (let [mx (max (apply max v) (M/abs (apply min v)))]
    (map #(/ % mx) v)))

(defmacro xconj [v vs]
  (if (instance? Seqable vs)
    `(loop [v# ~v vs# ~vs]
       (if-not (seq vs#)
         v#
         (recur (conj v# (first vs#)) (rest vs#))))
    `(conj ~v ~vs)))

(defn memoize-arg0 [f]
  (let [mem (atom {})]
    (fn [& args]
      (let [arg0 (first args)]
        (if-let [e (find @mem arg0)]
          (val e)
          (let [ret (apply f args)]
            (swap! mem assoc arg0 ret)
             ret))))))

;(def m (reduce (fn [m v] (assoc m v (* v v))) {} [1 2 3 4 5]))


(defn deflate-map [kw m]
  (loop [yx kw mx m]
    (if-not (seq yx)
      mx
      (recur (rest yx) (dissoc mx (first yx))))))

(defn fix-vals [m & args]
  (let [helper (fn [v] (if (keyword? v) (v m) v))]
    (map helper args)))

(defn defl-map-kw [m & args]
  (let [kw (filter keyword? args)]
    (deflate-map kw m)))