(ns maunaloa.models.mongodb.MongoWindowDressingModel
  (:gen-class
   :init init
   :state state
   :implements [maunaloax.models.ChartWindowDressingModel]
   ;:methods
   ; [
   ;  [getMongodbHost [] String]
   ;  [setMongodbHost [String] void]
   ;  [isCloud [] boolean]
   ;  [setCloud [boolean] void]
   ; ]
    )
  (:import
    [org.bson.types ObjectId]
    [com.mongodb MongoClient DBObject BasicDBObject]
    [maunaloax.models ChartWindowDressingModel]
    [maunaloax.domain MongoDBResult ChartWindowsDressingContext])
  (:require
    (maunaloa.service.mongodb
      [common :as MONGO]
      [comments :as comments]
      [fibonacci :as fib]
      [levels :as lvl])))

(defn -init []
  [[] (atom {})])

(defn -getMongodbHost [this]
  (let [s (.state this)]
    (:host @s)))

(defn -setMongodbHost [this host]
  (let [s (.state this)]
    (reset! s (assoc @s :host host))))

(defn -isCloud [this]
  (let [s (.state this)]
    (:cloud @s)))

(defn -setCloud [this value]
  (let [s (.state this)]
    (reset! s (assoc @s :cloud value))))

(defn dbconn [this]
  (if (= (-isCloud this) true)
    (MONGO/cloud-connection)
    (MONGO/local-connection (-getMongodbHost this))))

(comment do-fx [this
       ^ChartWindowsDressingContext ctx
       fib-fn
       lvl-fn]
  (let [collection (.getCollection ctx)]
    (if (> (bit-and ChartWindowDressingModel/MONGO_FIBONACCI collection) 0)
      (fib-fn (dbconn this) ctx))
    (if (> (bit-and ChartWindowDressingModel/MONGO_LEVELS collection) 0)
      (lvl-fn (dbconn this) ctx))))

(def FETCH 1)
(def SAVE 2)
(def UPDATE-COORD 3)


(defn get-fn [this ctx which-cmd]
  (let [collection (.getCollection ctx)]
    (cond
      (= collection ChartWindowDressingModel/MONGO_FIBONACCI)
        (cond
          (= which-cmd FETCH) (partial fib/fetch (dbconn this))
          (= which-cmd SAVE) (partial fib/save (dbconn this))
          (= which-cmd UPDATE-COORD) (partial fib/update-coord (dbconn this)))
      (= collection ChartWindowDressingModel/MONGO_LEVELS)
        (cond
          (= which-cmd FETCH) (partial lvl/fetch (dbconn this))
          (= which-cmd SAVE) (partial lvl/save (dbconn this))))))
          ;(= which-cmd UPDATE-COORD) (partial lvl/update-coord (dbconn this))))))



;      (if (= is-fetch true)
;                                                                (partial fib/fetch (dbconn this))
;                                                                (partial fib/save (dbconn this)))
;      (= collection ChartWindowDressingModel/MONGO_LEVELS) (if (= is-fetch true)
;                                                             (partial lvl/fetch (dbconn this))
;                                                             (partial lvl/save (dbconn this))))))


(defn -save [this
             ^ChartWindowsDressingContext ctx]
  ((get-fn this ctx SAVE) ctx))

(defn -fetch [this
             ^ChartWindowsDressingContext ctx]
  ((get-fn this ctx FETCH) ctx))

;WriteResult updateCoord(ObjectId id, DBObject p1, DBObject p2);
(defn -updateCoord [this
                    ^ChartWindowsDressingContext ctx]
  ((get-fn this ctx FETCH) ctx))

;Tuple<WriteResult,List<DBObject>> fetchComments(ObjectId id);
(defn -fetchComments [this,
                      ^ObjectId id]
  (comments/fetch (dbconn this) id))

;WriteResult addComment(ObjectId id, DBObject comment);
(defn -addComment [this,
                   ^ObjectId id,
                   ^String comment]
  (comments/save (dbconn this) id comment))
