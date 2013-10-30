(ns waimea.utils.logservice
  (:import
    [org.apache.log4j Logger]))

(def logger ^Logger (Logger/getLogger "waimea.utils"))

(defn fatal [msg]
  (.fatal ^Logger logger msg))

(defn error [msg]
  (.error ^Logger logger msg))

(defn warn [msg]
  (.warn ^Logger logger msg))

(defn info [msg]
  (.info ^Logger logger msg))

(defn debug [msg]
  (if (.isDebugEnabled ^Logger logger)
    (.debug ^Logger logger msg)))
