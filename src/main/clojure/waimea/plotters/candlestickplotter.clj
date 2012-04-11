(ns waimea.plotters.candlestickplotter
    (:require 
        [waimea.rulers.vruler :as VR]
        [waimea.rulers.dateruler :as HR])
    (:import (java.awt Color BasicStroke)))


(defn paint-candlestick [graphics x y-top y1 y2 y-btm is-bull]
    (.setColor graphics (:fg C/colors))
    ;(if (= is-bull :true)
    ;    (.setColor graphics (:fg C/colors))
    ;    (.setColor graphics (:cndl-bear C/colors)))

    (let [x1 (- x 3)
          x2 (+ x 3)]
        ;; the wicks
        (.drawLine graphics x y1 x y-top)  
        (.drawLine graphics x y2 x y-btm)  
        ;; the body
        (if (= is-bull :true)   
            (do
                (.drawLine graphics x1 y1 x2 y1)
                (.drawLine graphics x2 y1 x2 y2)
                (.drawLine graphics x2 y2 x1 y2)
                (.drawLine graphics x1 y2 x1 y1))
            (do
                (.setColor graphics (:cndl-bear C/colors))
                (.fillRect graphics x1 y1 (- x2 x1) (- y2 y1))))))


(defn candlestick-plotter-fn [dx-f opn-f hi-f lo-f spot-f]            
    (fn [data hr vr graphics]
        (doseq [p data] 
            (let [stroke (BasicStroke. 1.5)
                x (pix-x hr (dx-f p))
                opn-val (opn-f p)
                opn (pix-y vr opn-val)
                hi (pix-y vr (hi-f p))
                lo (pix-y vr (lo-f p))
                spot-val (spot-f p)
                spot (pix-y vr spot-val)
             ]
             (.setStroke graphics stroke)
             (if (> spot-val opn-val)    
                (paint-candlestick graphics x hi spot opn lo :true)
                (paint-candlestick graphics x hi opn spot lo :false))))))