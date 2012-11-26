(ns waimea.rulers.dateruler
  (:require [waimea.utils.commonutils :as U])
  (:import
    [javafx.scene.canvas GraphicsContext]
    [javafx.scene.paint Color]
    [java.util Date]
    [java.text SimpleDateFormat]))

(def date-format (SimpleDateFormat. "MM/yy"))

(defn calibrate-dateruler [q]                          
  (let [hr (:hruler q)
        ul (:ul q)
        lr (:lr q)
        w (- (:x lr) (:x ul))
        ppx (float (/ w (U/diff-days (:start hr) (:end hr))))]
    (assoc hr
        :x0 (:x ul)
        :x1 (:x lr)
        :y0 (:y ul)
        :y1 (:y lr)
        :ppx ppx)))

(defn pix-x [ruler ^Double value]
  (let [h-diff (U/diff-days (:start ruler) value)]
    (+ (:x0 ruler)
       (int
          (* (:ppx ruler) h-diff)))))

(defn plot-date [^Date d ruler ^GraphicsContext g]
  (let [px (pix-x ruler d)
        y0 (:y0 ruler)
        y1 (:y1 ruler)
        legend (:legend ruler)]
    (if (= legend :true)
        (.strokeText g (.format ^SimpleDateFormat date-format d) px (+ (int y1) 15)))
    (.strokeLine g px y0 px y1)))

(defn plot-dateruler [ruler ^GraphicsContext g]
  (let [pix (U/find-dates-between (:start ruler) (:end ruler) :month)]
    (doseq [m pix]
      (plot-date m ruler g))))
