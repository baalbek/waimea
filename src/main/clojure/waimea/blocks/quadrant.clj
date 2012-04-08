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

(defn plot-quadrant [q ^Graphics g]
  ;(DR/calibrate-dateruler q)
  (let [vr (VR/calibrate-vruler q)
        vr-plot-fn (:plot-fn vr)
        ;hr @(:hruler q)
        plts (:plotters q)]
          (vr-plot-fn vr g)
          ;(DR/plot-dateruler hr g)
          ;(doseq [plt plts] (do-plotter plt hr vr g))
          ))

(defn quadrant-height [quadrant margin ^JComponent cp]
  (- (* (:pct quadrant) (.getHeight cp)) margin))

