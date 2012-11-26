(ns waimea.plotters.lineplotter
  (:require
    [waimea.common :as COMM]
    [waimea.rulers.vruler :as VR]
    [waimea.rulers.dateruler :as HR])
  (:import
    [javafx.scene.canvas GraphicsContext]
    [javafx.scene.paint Color]))

(defn single-line-plotter [values dx color stroke]
  (fn [hr vr ^GraphicsContext graphics]
    (let [vdx (partition 2 1 (map list (rest values) (rest dx)))]
      (doseq [i vdx]
        (let [p0 (first i)
              p1 (second i)
          x0 (HR/pix-x hr (second p0))
          y0 (VR/pix-y vr (first p0))
          x1 (HR/pix-x hr (second p1))
          y1 (VR/pix-y vr (first p1))]
        (.strokeLine graphics x0 y0 x1 y1))))))

(defn volume-plotter [values dx color]
  (fn [hr vr ^GraphicsContext graphics]
    (doseq [v (map list values dx)]
      (let [ cur-val (first v)
             cur-dx (second v)
             x (HR/pix-x hr cur-dx)
             y0 (VR/pix-y vr 0.0)
             y1 (VR/pix-y vr cur-val)]
        (.strokeLine graphics x y0 x y1)))))