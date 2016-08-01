(ns maunaloa.service.mongodb.fibonacci
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


(defn create-point
  ([year month day y-val]
    (let [dx (util/new-date year month day)]
      (create-point dx y-val)))
  ([dx y-val]
    (let [result (BasicDBObject. "x" dx)]
      (.append result "y" y-val)
      result)))

(defn create-item [^String tix
                   loc
                   ^DBObject p1
                   ^DBObject p2]
  (let [result (BasicDBObject. "tix" tix)]
    (doto result
      (.append "active" true)
      (.append "loc" loc)
      (.append "p1" p1)
      (.append "p2" p2))))

(defn fetch [^DBApiLayer conn
                ^ChartWindowsDressingContext ctx]
  (let [coll ^DBCollection (.getCollection conn "fibonacci")
        query (BasicDBObject. "tix" (.getTicker ctx))]
    (doto query
      (.append "loc" (.getLoc ctx))
      (.append "active" true))
    (.toArray ^DBCursor (.find coll query))))

(defn save [^DBApiLayer conn
             ^ChartWindowsDressingContext ctx]
  (let [coll ^DBCollection (.getCollection conn "fibonacci")
        ticker ^String (.getTicker ctx)
        loc ^long (.getLoc ctx)
        p1 ^DBObject (.getP1 ctx)
        p2 ^DBObject (.getP2 ctx)
        result ^BasicDBObject (create-item ticker loc p1 p2)
        server-result (.save coll result)]
    (MongoDBResult. result server-result)))

(comment fetch [^DBApiLayer conn
             ^String ticker
             loc
             ^Date from-date
             ^Date to-date]
  (let [coll (.getCollection conn "fibonacci")
        query (BasicDBObject. "tix" ticker)]
    (doto query
      (.append "loc" loc)
      (.append "active" true))
    (if-not (nil? from-date)
      (.put query "p1.x" (BasicDBObject. "$gt" from-date)))
    (if-not (nil? to-date)
      (.put query "p2.x" (BasicDBObject. "$lt" to-date)))
    (.toArray ^DBCursor (.find coll query))))

(comment save [^DBApiLayer conn
            ^String ticker
            loc
            ^DBObject p1
            ^DBObject p2]
  (let [coll (.getCollection conn "fibonacci")
        result (create-item ticker loc p1 p2)
        server-result ^WriteResult (.save coll result)]
    (MongoDBResult. result server-result)))


;db.fibonacci.update({_id : ObjectId("525d841b44ae19e5186a95c6")}, {$set : {loc: 3}})

(defn update-coord [^DBApiLayer conn
                    ^ChartWindowsDressingContext ctx])

(comment update-coord [^DBApiLayer conn
                    ^ObjectId id
                    ^DBObject p1
                    ^DBObject p2]
  (let [coll (.getCollection conn "fibonacci")
        set-obj (BasicDBObject. "$set" (BasicDBObject. "p1" p1))
        ;query (BasicDBObject. "_id" (ObjectId. "525d841b44ae19e5186a95c6"))]
        query (BasicDBObject. "_id" id)]
    (.append set-obj "$set" (BasicDBObject. "p2" p2))
    (.update coll query set-obj)))




