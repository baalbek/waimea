(ns waimea.utils.recordutils)

;;-------------------------------------------------------------
;;------------------------ Record utils -----------------------
;;-------------------------------------------------------------

(defn deflate-map [kw m]
  (loop [yx kw mx m]
    (if-not (seq yx)
      mx
      (recur (rest yx) (dissoc mx (first yx))))))

(defn create-record-with-map [clazz m & args]
  (let [ctr (symbol (str clazz "."))
        helper (fn [v] (if (keyword? v) (v m) v))
        fix-vals (map helper args)
        kw (filter keyword? args)
        rest-map (deflate-map kw m)]
    (eval `(~ctr ~@fix-vals {} ~rest-map))))
