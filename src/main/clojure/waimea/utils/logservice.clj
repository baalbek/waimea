(ns waimea.utils.logservice
  (:import
    [org.apache.log4j Logger]))

(def logger (Logger/getLogger "waimea.utils"))

(defn fatal [msg]
  (.fatal logger msg))

(defn error [msg]
  (.error logger msg))

(defn warn [msg]
  (.warn logger msg))

(defn info [msg]
  (.info logger msg))

(defn debug [msg]
  (if (.isDebugEnabled logger)
    (.debug logger msg)))
