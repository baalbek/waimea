(ns waimea.rulers.dateruler
    (:require [waimea.utils :as U])
    (:import (java.awt Color Graphics2D)
             (java.util Date)
             (java.text SimpleDateFormat)))

(def date-format (SimpleDateFormat. "MM/yyyy"))

(defn calibrate-dateruler [q ul lr]                          
    (let [hr @(:hruler q)                   
          w (- (:x lr) (:x ul))               
          ppx (float (/ w (U/diff-days (:start hr) (:end hr))))]
        (dosync (ref-set (:hruler q) 
                    (assoc hr 
                        :x0 (:x ul)
                        :x1 (:x lr)
                        :y0 (:y ul)
                        :y1 (:y lr)
                        :ppx ppx)))))

(defn pix-x [ruler ^Double value]
    (let [
          h-diff (U/diff-days (:start ruler) value)
         ]
          (+ (:x0 ruler) (int (* (:ppx ruler) h-diff)))
       ))  

(defn plot-date [^Date d ruler ^Graphics2D g]
    (let [px (pix-x ruler d)
          y0 (:y0 ruler)
          y1 (:y1 ruler)
          legend (:legend ruler)]
        (if (= legend :true)
            (.drawString g (.format date-format d) px (+ (int y1) 15)))
        (.drawLine g px y0 px y1)))

(defn plot-dateruler [ruler ^Graphics2D g]
    (let [pix (U/find-dates-between (:start ruler) (:end ruler) :month)]
        (doseq [m pix]
            (plot-date m ruler g))))
