(ns waimea.rulers.dateruler
  (:require [waimea.utils.commonutils :as CUTIL]
            [waimea.protocols.chart :as CH])
  (:import
    [javafx.scene.canvas GraphicsContext]
    [javafx.scene.paint Color]
    [java.util Date]
    [java.text SimpleDateFormat]))

(def date-format (SimpleDateFormat. "MM/yy"))

(defrecord DateRuler [x0 start ppx])

(extend-protocol CH/IRuler
  DateRuler
  (calcPix
    ([this value]
      (let [h-diff (CUTIL/diff-days (.start this) value)]
        (+ (.x0 this)
          (* (.ppx this) h-diff))))
    ([this value f]
      (f (CH/calcPix this value))))
  (calcValue [this pix]))

(defn calibrate-dateruler [q]                          
  (let [hr (:hruler q)
        ul (:ul q)
        lr (:lr q)
        w (- (:x lr) (:x ul))
        ppx (float (/ w (CUTIL/diff-days (:start hr) (:end hr))))
        m (assoc hr
            :x0 (:x ul)
            :x1 (:x lr)
            :y0 (:y ul)
            :y1 (:y lr)
            :ppx ppx)
        dm (CUTIL/defl-map-kw m :x0 :start :ppx)]
    (DateRuler. (:x0 m) (:start m) ppx {} dm)))

(defn plot-date [^Date d ruler ^GraphicsContext g]
  (let [px (CH/calcPix ruler d)
        y0 (:y0 ruler)
        y1 (:y1 ruler)
        legend (:legend ruler)]
    (if (= legend true)
      (let [t (.getLineWidth g)]
        (.setLineWidth g 1.0)
        (.strokeText g (.format ^SimpleDateFormat date-format d) (+ 0.5 (int px)) (+ 0.5 (+ (int y1) 15)))
        (.setLineWidth g t)))
    (.strokeLine g px y0 px y1)))

(defn plot-dateruler [ruler ^GraphicsContext g]
  (.setLineWidth g 0.25)
  (let [pix (CUTIL/find-dates-between (:start ruler) (:end ruler) :month)]
    (doseq [m pix]
      (plot-date m ruler g))))
