(ns waimea.blocks.quadrant
    (:require 
        [waimea.rulers.vruler :as VR]
        [waimea.rulers.dateruler :as DR])
    (:import 
        (javax.swing JComponent)
        (java.awt Color Graphics BasicStroke)))

(defn do-plotter [plt hr vr g]
    (let [data (:data plt)
          funx (:funx plt)]
        (doseq [f funx]
            (f data hr vr g))))

(defn plot-quadrant [q ul lr ^Graphics g]
  (VR/calibrate-vruler q ul lr)
  (DR/calibrate-dateruler q ul lr)
  (let [vr @(:vruler q)
        vr-plot-fn (:plot-fn vr)
        hr @(:hruler q)
        plts (:plotters q)]
          ;(VR/plot-vruler vr g)
          (vr-plot-fn vr g)
          (DR/plot-dateruler hr g)
          (doseq [plt plts]
             (do-plotter plt hr vr g))))

(defn quadrant-height [quadrant margin ^JComponent cp]
  (- (* (:pct quadrant) (.getHeight cp)) margin))

