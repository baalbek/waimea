(ns waimea.plotters.lineplotter
    (:require 
        [waimea.common :as COMM]
        [waimea.rulers.vruler :as VR]
        [waimea.rulers.dateruler :as HR])
    (:import (java.awt Color BasicStroke)))

(defn single-line-plotter [vals dx color]
    (fn [hr vr graphics]
        (.setColor graphics color)
        (.setStroke graphics (COMM/strokes :lineplot))
        (let [vdx (partition 2 1 (map list (rest vals) (rest dx)))]
              (doseq [i vdx] 
                    (let [p0 (first i)
                          p1 (second i)
                        x0 (HR/pix-x hr (second p0))
                        y0 (VR/pix-y vr (first p0))
                        x1 (HR/pix-x hr (second p1))
                        y1 (VR/pix-y vr (first p1))]
                    (.drawLine graphics x0 y0 x1 y1))))))
