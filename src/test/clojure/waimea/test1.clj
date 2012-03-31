(ns waimea.test1
    (:require [waimea.swoosh :as Swoosh])
    (:use clojure.test))

(is (= (Swoosh/jada 2 2) 4))

(println "Hi")
    
