(ns waimea.blocks.quadrant
    (:require 
        [waimea.utils.logservice :as LOG]
        [waimea.rulers.vruler :as VR]
        [waimea.rulers.dateruler :as DR])
    (:import
      [javafx.geometry Point2D]
      [javafx.scene.canvas Canvas GraphicsContext]
      [maunaloax.views.chart DefaultDateRuler DefaultVRuler]))

(defn do-plotter [plt hr vr g]
    (let [data (:data plt)
          funx (:funx plt)]
        (doseq [f funx]
            (f data hr vr g))))

(defn plot-quadrant [^GraphicsContext g q]
  (let [vr (VR/calibrate-vruler q)
        hr (DR/calibrate-dateruler q)
        ul (:ul q)
        lr (:lr q)]
          ((:plot-fn vr) vr g)
          (DR/plot-dateruler hr g)
          (doseq [p (:plotters q)]
            (p hr vr g))
    (if-let [snapu (:snap-unit q)]
      [(DefaultDateRuler. (:x0 hr) (:start hr) (:end hr) (:ppx hr) snapu)
       (DefaultVRuler.
            (Point2D. (:x ul) (:y ul))
            (Point2D. (:x lr) (:y lr))
            (:ppx vr)
            (:max vr))]
      (do
        (LOG/debug (str "Start date: " (:start hr) ", end date: " (:end hr)))
        [nil nil]))))

(defn quadrant-height [quadrant margin ^Canvas c]
  (- (* (:pct quadrant) (.getHeight c)) margin))

