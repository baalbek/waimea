(ns waimea.plotters.candlestickplotter
    (:require 
        [waimea.common :as C]
        (waimea.rulers [vruler :as VR] [dateruler :as HR])
        [waimea.protocols.financial :as F])
    (:import
      [javafx.scene.canvas GraphicsContext]
      [javafx.scene.paint Color]
      [oahu.financial.beans StockBean]))

(defn paint-candlestick [^GraphicsContext graphics
                         x
                         y-top
                         y1
                         y2
                         y-btm
                         is-bull]
  (let [x1 (- x 3)
        x2 (+ x 3)]

    (doto graphics
      ;; the wicks
      (.beginPath)
      (.moveTo x y1)
      (.lineTo x y-top)
      (.moveTo x y2)
      (.lineTo x y-btm)
      ;; the body
      (.moveTo x1 y1)
      (.lineTo x2 y1)
      (.lineTo x2 y2)
      (.lineTo x1 y2)
      (.lineTo x1 y1))
    (if (= is-bull :false)
      (doto graphics
        (.setFill (Color/rgb 255 0 0))
        (.fillRect x1 y1 (- x2 x1) (- y2 y1))))
    (doto graphics
      (.closePath)
      (.stroke))))

(defn candlestick-plotter [data]
  (fn [hr vr ^GraphicsContext graphics]
    (.setLineWidth graphics 0.5)
    (.setStroke graphics (Color/rgb 0 0 0))
    (let [jitter-fn #(+ 0.5 (int %))]
      (doseq [^StockBean p data]
        (let [x (HR/pix-x hr (F/dx p) jitter-fn)
              opn-val (F/opn p)
              opn (VR/pix-y vr opn-val jitter-fn)
              hi (VR/pix-y vr (F/hi p) jitter-fn)
              lo (VR/pix-y vr (F/lo p) jitter-fn)
              spot-val (F/spot p)
              spot (VR/pix-y vr spot-val jitter-fn)
              ]
          (if (> spot-val opn-val)
            (paint-candlestick graphics x hi spot opn lo :true)
            (paint-candlestick graphics x hi opn spot lo :false)))))))




