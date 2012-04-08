(ns waimea.rulers.vruler
    (:import (java.awt Color)))

(defn vruler-y-coords [ul lr increments]
    (let [
            diff (- (:y lr) (:y ul))
            gap (/ diff increments)
            y0 (:y ul)
         ]
          (for [i (range (+ 1 increments))] (int (+ (* i gap) y0)))))

(defn calibrate-vruler [q]                          
    (let [vr @(:vruler q)       
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
            :y (vruler-y-coords ul lr segs))))

(defn pix-y [ruler value]
    (let [
          v-diff (- (:max ruler) value)
         ]
          (+ (:y0 ruler) (int (* (:ppx ruler) v-diff)))
       ))  

(defn val-y [ruler px]
    (let [pxv (/ 1.0 (:ppx ruler))
          mx (:max ruler) 
          y0 (:y0 ruler)
          v (* (- px y0) pxv)]
          (- mx v)))

(defn plot-vruler [ruler g]
    (let [x0 (:x0 ruler)
          x1 (:x1 ruler)]
        (doseq [cur-y (:y ruler)]
            (.setColor g Color/BLACK)
            (.drawString g (format "%.2f" (val-y ruler cur-y)) 5 (- cur-y 3))
            (.setColor g (:color ruler))
            (.drawLine g x0 cur-y x1 cur-y))))

(defn plot-vol-ruler [ruler g]
    (let [x0 (:x0 ruler)
          x1 (:x1 ruler)
          max-vol (:max ruler)]
        (doseq [cur-y (:y ruler)]
            (.setColor g Color/BLACK)
            (.drawString g (format "%.2f" (/ (val-y ruler cur-y) max-vol)) 5 (- cur-y 3))
            (.setColor g (:color ruler))
            (.drawLine g x0 cur-y x1 cur-y))))
