(ns waimea.rulers.vruler
  (:import
    [javafx.scene.canvas GraphicsContext]
    [javafx.scene.text Text]
    [javafx.scene.paint Color])
  (:require
    [waimea.utils.fxutils :as FX]))

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
        (assoc vr 
            :ppx ppx
            :x0 (:x ul)
            :x1 (:x lr)
            :y0 (:y ul)
            :y1 (:y lr)
            :y (vruler-y-coords ul lr segs))))

;(defn pix-y [ruler value]
;    (let [v-diff (- (:max ruler) value)]
;        (+ (:y0 ruler) (* (:ppx ruler) v-diff))))

;(defmacro pix-y [ruler value & rest]
(defmacro pix-y [& args]
  (let [[ruler value f] args
         base-form `(let [v-diff# (- (:max ~ruler) ~value)]
                  (+ (:y0 ~ruler) (* (:ppx ~ruler) v-diff#)))]
    (if (= f nil)
      (do (println "f is nil: " ruler value) base-form)
      (do (println "f is NOT nil: " f) `(~f ~base-form)))))


(defn val-y [ruler px]
  (let [pxv (/ 1.0 (:ppx ruler))
    mx (:max ruler)
    y0 (:y0 ruler)
    v (* (- px y0) pxv)]
    (- mx v)))

(defn plot-vruler [ruler ^GraphicsContext g]
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
    (let [x0 (:x0 ruler)
          x1 (:x1 ruler)
          max-vol (:max ruler)]
        (doseq [cur-y (:y ruler)]
            ;(.setColor g Color/BLACK)
            (.strokeText g (format "%.2f" (/ (val-y ruler cur-y) max-vol)) 5 (- cur-y 3))
            ;(.setColor g (:color ruler))
            ;(.strokeLine g x0 cur-y x1 cur-y)
          (FX/drawLine  g x0 cur-y x1 cur-y))))
