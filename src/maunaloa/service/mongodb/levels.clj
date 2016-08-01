(ns maunaloa.service.mongodb.levels
  (:import
    [java.util Date]
    [com.mongodb
     BasicDBObject
     DBObject
     DBCursor
     DBApiLayer
     DBCollection
     WriteResult]
    [org.bson.types ObjectId]
    [maunaloax.domain MongoDBResult ChartWindowsDressingContext])
  (:require
    (maunaloa.utils [commonutils :as util])))

(defn create-item [^String tix
                   loc
                   ^double value]
  (let [result (BasicDBObject. "tix" tix)]
    (doto result
      (.append "active" true)
      (.append "loc" loc)
      (.append "value" value))))

(defn save [^DBApiLayer conn
            ^ChartWindowsDressingContext ctx]
  (let [coll ^DBCollection (.getCollection conn "levels")
        ticker ^String (.getTicker ctx)
        loc ^long (.getLoc ctx)
        value ^double (.getValue ctx)
        result ^BasicDBObject (create-item ticker loc value)
        server-result ^WriteResult (.save coll result)]
    (MongoDBResult. result server-result)))

(defn fetch [^DBApiLayer conn
             ^ChartWindowsDressingContext ctx]
  (let [coll ^DBCollection (.getCollection conn "levels")
        query (BasicDBObject. "tix" (.getTicker ctx))]
    (doto query
      (.append "loc" (.getLoc ctx))
      (.append "active" true))
    (.toArray ^DBCursor (.find coll query))))

(defn update-coord [^DBApiLayer conn
                    ^ChartWindowsDressingContext ctx])