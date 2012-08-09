(ns waimea.plotters.lineplotter
  (:require
    [waimea.common :as COMM]
    [waimea.rulers.vruler :as VR]
    [waimea.rulers.dateruler :as HR])
  (:import (java.awt Color BasicStroke Graphics2D)))

(defn single-line-plotter [values dx color]
  (fn [hr vr ^Graphics2D graphics]
    (.setColor graphics color)
    (.setStroke graphics (COMM/strokes :lineplot))
    (let [vdx (partition 2 1 (map list (rest values) (rest dx)))]
      (doseq [i vdx]
        (let [p0 (first i)
              p1 (second i)
          x0 (HR/pix-x hr (second p0))
          y0 (VR/pix-y vr (first p0))
          x1 (HR/pix-x hr (second p1))
          y1 (VR/pix-y vr (first p1))]
        (.drawLine graphics x0 y0 x1 y1))))))

(defn volume-plotter [values dx color]
  (fn [hr vr ^Graphics2D graphics]
    (.setColor graphics color)
    (.setStroke graphics (COMM/strokes :lineplot))
    (doseq [v (map list values dx)]
      (let [ cur-val (first v)
             cur-dx (second v)
             x (HR/pix-x hr cur-dx)
             y0 (VR/pix-y vr 0.0)
             y1 (VR/pix-y vr cur-val)]
        (.drawLine graphics x y0 x y1)))))