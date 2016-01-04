(ns waimea.plotters.candlestickplotter
    (:require 
        [waimea.common :as C]
        [waimea.protocols.chart :as CHART]
        (waimea.rulers [vruler :as VR] [dateruler :as HR])
        [waimea.protocols.financial :as F])
    (:import
      [javafx.scene.canvas GraphicsContext]
      [javafx.scene.paint Color]
      [oahu.financial StockPrice]))

(defn paint-candlestick [^GraphicsContext graphics
                         x
                         y-top
                         y1
                         y2
                         y-btm
                         is-bull
                         bear-color]
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
        ;(.setFill (Color/rgb 255 0 0))
        (.setFill bear-color)
        (.fillRect x1 y1 (- x2 x1) (- y2 y1))))
    (doto graphics
      (.closePath)
      (.stroke))))

(defn candlestick-plotter [data fg-color bear-color]
  (fn [hr vr ^GraphicsContext graphics]
    (.setLineWidth graphics 0.5)
    ;(.setStroke graphics (Color/rgb 0 0 0))
    (.setStroke graphics fg-color)
    (let [jitter-fn #(+ 0.5 (int %))]
      (doseq [^StockPrice p data]
        (let [x (CHART/calcPix hr (F/dx p) jitter-fn)
              opn-val (F/opn p)
              opn (CHART/calcPix vr opn-val jitter-fn)
              hi (CHART/calcPix vr (F/hi p) jitter-fn)
              lo (CHART/calcPix vr (F/lo p) jitter-fn)
              spot-val (F/spot p)
              spot (CHART/calcPix  vr spot-val jitter-fn)
              ]
          (if (> spot-val opn-val)

            (paint-candlestick graphics x hi spot opn lo :true bear-color)
            (paint-candlestick graphics x hi opn spot lo :false bear-color)))))))




