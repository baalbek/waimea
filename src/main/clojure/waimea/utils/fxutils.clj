(ns waimea.utils.fxutils
  (:import
    [javafx.scene.canvas GraphicsContext]))


(defn drawLine
  ([^GraphicsContext g ^Double x0 ^Double y0 ^Double x1 ^Double y1]
    (doto g
    (.beginPath)
    (.moveTo x0 y0)
    (.lineTo x1 y1)
    (.closePath)
    (.stroke)))
  ([^GraphicsContext g ^Double x ^Double y]
    (doto g
      (.beginPath)
      (.lineTo x y)
      (.closePath)
      (.stroke))))
