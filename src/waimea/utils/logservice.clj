(ns waimea.utils.logservice
  (:import
    [org.apache.log4j Logger PropertyConfigurator]
    [org.apache.commons.logging LogFactory]
    [java.util Properties]))

(def logger ^Logger (Logger/getLogger "waimea.utils"))

(defn initLog4j [& [xml-name]]
  (let [cur-xml-name (if (nil? xml-name) "/log4j.xml" xml-name)
        lf (LogFactory/getFactory)
        props (Properties.)
        clazz (.getClass props)
        resource (.getResourceAsStream clazz cur-xml-name)]
    (.setAttribute lf "org.apache.commons.logging.Log" "org.apache.commons.logging.impl.NoOpLog")
    (.load props resource)
    (PropertyConfigurator/configure props)))

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
