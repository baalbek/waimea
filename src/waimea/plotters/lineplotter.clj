(ns waimea.plotters.lineplotter
  (:require
    [waimea.common :as COMM]
    [waimea.protocols.chart :as CHART]
    (waimea.rulers
      [vruler :as VR]
      [dateruler :as HR]))
  (:import
    [javafx.scene.canvas GraphicsContext]))

;(defn single-line-plotter [values dx stroke line-width]
(defn single-line-plotter [{:keys [values
                                   dx
                                   stroke
                                   line-width]
                            :or {line-width 0.5}}]
  (fn [hr vr ^GraphicsContext graphics]
    (.setLineWidth graphics line-width)
    (.setStroke graphics stroke)
    (let [vdx (partition 2 1 (map list values dx))]
      (doseq [i vdx]
        (let [p0 (first i)
              p1 (second i)
          x0 (CHART/calcPix hr (second p0))
          y0 (CHART/calcPix vr (first p0))
          x1 (CHART/calcPix hr (second p1))
          y1 (CHART/calcPix vr (first p1))]
        (.strokeLine graphics x0 y0 x1 y1))))))

(defn volume-plotter [values dx color]
  (fn [hr vr ^GraphicsContext graphics]
    (.setLineWidth graphics 1.0)
    (.setStroke graphics color)
    (doseq [v (map list values dx)]
      (let [ cur-val (first v)
             cur-dx (second v)
             x (CHART/calcPix hr cur-dx)
             y0 (CHART/calcPix vr 0.0)
             y1 (CHART/calcPix vr cur-val)]
        (.strokeLine graphics x y0 x y1)))))

;(defn vruler-demo [vr]
;  (CHART/calcPix vr 93.0))