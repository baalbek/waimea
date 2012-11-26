(ns waimea.blocks.quadrant
    (:require 
        [waimea.rulers.vruler :as VR]
        [waimea.rulers.dateruler :as DR])
    (:import
      [javafx.scene.canvas Canvas GraphicsContext]
      [javafx.scene.paint Color]))

(defn do-plotter [plt hr vr g]
    (let [data (:data plt)
          funx (:funx plt)]
        (doseq [f funx]
            (f data hr vr g))))

(defn plot-quadrant [^GraphicsContext g q]
  (let [vr (VR/calibrate-vruler q)
        hr (DR/calibrate-dateruler q)]
          ((:plot-fn vr) vr g)
          (DR/plot-dateruler hr g)
          (doseq [p (:plotters q)] 
            (p hr vr g))
          [hr vr]))

(defn quadrant-height [quadrant margin ^Canvas c]
  (- (* (:pct quadrant) (.getHeight c)) margin))

