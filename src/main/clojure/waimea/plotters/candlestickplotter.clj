(ns waimea.plotters.candlestickplotter
    (:require 
        [waimea.common :as C]
        (waimea.rulers [vruler :as VR] [dateruler :as HR])
        [waimea.protocols.financial :as F])
    (:import
      [javafx.scene.canvas GraphicsContext]
      [javafx.scene.paint Color]
      [oahu.financial.beans StockBean]))


(comment
(defn paint-candlestick [^GraphicsContext graphics x y-top y1 y2 y-btm is-bull]
    ;(.setColor graphics (:fg C/colors))
    (let [x1 (- x 3)
          x2 (+ x 3)]
        ;; the wicks
        (.strokeLine graphics x y1 x y-top)
        (.strokeLine graphics x y2 x y-btm)
        ;; the body
        (if (= is-bull :true)   
            (do
                (.strokeLine graphics x1 y1 x2 y1)
                (.strokeLine graphics x2 y1 x2 y2)
                (.strokeLine graphics x2 y2 x1 y2)
                (.strokeLine graphics x1 y2 x1 y1))
            (do
                (.fillRect graphics x1 y1 (- x2 x1) (- y2 y1)))))))

(defn paint-candlestick [^GraphicsContext graphics
                         x
                         y-top
                         y1
                         y2
                         y-btm
                         is-bull]
  (let [x1 (- x 3)
        x2 (+ x 3)]
    ;; the wicks
    (doto graphics
      (.beginPath)
      (.moveTo x y1)
      (.lineTo x y-top)
      (.moveTo x y2)
      (.lineTo x y-btm))
    ;; the body
    (if (= is-bull :true)
      (doto graphics
        (.moveTo x1 y1)
        (.lineTo x2 y1)
        (.lineTo x2 y2)
        (.lineTo x1 y2)
        (.lineTo x1 y1))
      (doto graphics
        (.setStroke (Color/rgb 255 0 0))
        (.strokeRect x1 y1 (- x2 y1) (- y2 y1))
        (.setStroke (Color/rgb 255 255 255))))
    (doto graphics
      (.closePath)
      (.stroke))))

(comment
    (doto graphics
      (.moveTo x1 y1)
      (.lineTo x2 y1)
      (.lineTo x2 y2)
      (.lineTo x1 y2)
      (.lineTo x1 y1)
      (.closePath)
      (.stroke)))

(defn candlestick-plotter [data]
  (fn [hr vr ^GraphicsContext graphics]
    (let [jitter-fn #(+ 0.5 (int %))]
      (doseq [^StockBean p data]
        (let [;stroke (BasicStroke. 1.5)
              x (HR/pix-x hr (F/dx p))
              opn-val (F/opn p)
              opn (VR/pix-y vr opn-val jitter-fn)
              hi (VR/pix-y vr (F/hi p) jitter-fn)
              lo (VR/pix-y vr (F/lo p) jitter-fn)
              spot-val (F/spot p)
              spot (VR/pix-y vr spot-val jitter-fn)
              ]
          ;(.setStroke graphics stroke)
          (if (> spot-val opn-val)
            (paint-candlestick graphics x hi spot opn lo :true)
            (paint-candlestick graphics x hi opn spot lo :false)))))))




