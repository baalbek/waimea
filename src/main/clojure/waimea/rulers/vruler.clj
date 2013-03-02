(ns waimea.rulers.vruler
  (:import
    [javafx.scene.canvas GraphicsContext]
    [javafx.scene.text Text]
    [javafx.scene.paint Color])
  (:require
    [waimea.utils.fxutils :as FX]
    [waimea.protocols.chart :as CH]))


(defrecord VRuler [y0 max ppx])


(comment
(extend-protocol CH/IRuler
  VRuler
  (calcPix
    ([this value] 3)
    ([this value f]
      (f (CH/calcPix this value))))
  (calcValue [this pix]))
)

(extend-protocol CH/IRuler
  VRuler
  (calcPix
    ([this value]
      (let [v-diff (- (.max this) value)]
        (+ (.y0 this) (* (.ppx this) v-diff))))
    ([this value f]
      (f (CH/calcPix this value))))
  (calcValue [this pix]
    (let [pxv (/ 1.0 (.ppx this))
          v (* (- pix (.y0 this)) pxv)]
        (- (.max this) v))))

(defn vruler-y-coords [ul lr increments]
    (let [
            diff (- (:y lr) (:y ul))
            gap (/ diff increments)
            y0 (:y ul)
         ]
          (for [i (range (+ 1 increments))] (+ 0.5 (int (+ (* i gap) y0))))))

(defn calibrate-vruler [q]                          
    (let [vr (:vruler q)       
          ul (:ul q)
          lr (:lr q)
          segs (:segs vr)
          h (- (:y lr) (:y ul))               
          ppx (/ h (- (:max vr) (:min vr)))]
    (VRuler. (:y ul) (:max vr) ppx {} (assoc vr
                                        :ppx ppx
                                        :x0 (:x ul)
                                        :x1 (:x lr)
                                        :y0 (:y ul)
                                        :y1 (:y lr)
                                        :y (vruler-y-coords ul lr segs)))))

;(defmacro pix-y [& args]
;  (let [[ruler value f] args
;         base-form `(let [v-diff# (- (:max ~ruler) ~value)]
;                  (+ (:y0 ~ruler) (* (:ppx ~ruler) v-diff#)))]
;    (if (= f nil)
;      base-form
;      `(~f ~base-form))))

(defn pix-y
  ([ruler value]
    (let [v-diff (- (:max ruler) value)]
      (+ (:y0 ruler) (* (:ppx ruler) v-diff))))
  ([ruler value f]
    (f (pix-y ruler value))))


(defn val-y [ruler px]
  (let [pxv (/ 1.0 (:ppx ruler))
    mx (:max ruler)
    y0 (:y0 ruler)
    v (* (- px y0) pxv)]
    (- mx v)))

(defn plot-vruler [ruler ^GraphicsContext g]
  (.setLineWidth g 0.25)
  (.setStroke g Color/DARKSLATEGRAY)
  (let [x0 (:x0 ruler)
        x1 (:x1 ruler)]
    (doseq [cur-y (:y ruler)]
      (let [t (.getLineWidth g)]
        (.setLineWidth g 1.0)
        (.strokeText g (format "%.2f" (val-y ruler cur-y)) 5.5 (- cur-y 3.0))
        (.setLineWidth g t))
      ;(Text 5 (- cur-y 3) (format "%.2f" (val-y ruler cur-y)))
      (.strokeLine g x0 cur-y x1 cur-y))))
    ;(FX/drawLine g x0 cur-y x1 cur-y))))

(defn plot-vol-ruler [ruler ^GraphicsContext g]
  (.setStroke g Color/DARKSLATEGRAY)
  (let [x0 (:x0 ruler)
        x1 (:x1 ruler)
        max-vol (:max ruler)]
      (doseq [cur-y (:y ruler)]
          ;(.setColor g Color/BLACK)
        (if (= (:legend ruler) true)
          (.strokeText g (format "%.2f" (/ (val-y ruler cur-y) max-vol)) 5 (- cur-y 3)))
          ;(.setColor g (:color ruler))
          ;(.strokeLine g x0 cur-y x1 cur-y)
        (FX/drawLine  g x0 cur-y x1 cur-y))))

