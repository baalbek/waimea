(ns waimea.blocks.block
    (:require 
        [waimea.rulers.vruler :as VR]
        [waimea.utils :as U]
        [waimea.common :as C]))

(defn create-block [[data-min data-max]
                    & {:keys [start-date 
                              end-date 
                              plotters 
                              pct     
                              gap 
                              legend 
                              vr-plot-fn 
                              vr-segs
                              tolerance] 
                    :or {start-date nil
                         end-date nil
                         plotters []
                         gap 0
                         legend :false
                         vr-plot-fn VR/plot-vruler 
                         vr-segs 10
                         tolerance 1.1}}]
                {:vruler (ref 
                            {:min (/ data-min tolerance) 
                             :max (* data-max tolerance)
                             :color (:bg C/colors)
                             :plot-fn vr-plot-fn
                             :segs vr-segs})
                :hruler (ref 
                            {:start (U/date-add-days start-date -10) 
                             :end (U/date-add-days end-date 20)
                             :color (:bg C/colors)
                             :legend legend})
                :pct pct
                :gap gap
                :plotters plotters})

(defn block-chain [& {:keys [qs h x0 x1 y0]
                                 :or {x0 0.0
                                      y0 0.0}}]
    (loop [result [], q qs, cur-y y0]
        (if-not (seq q)
            result
            (let [cur-q (first q)
                  ul {:x x0 :y (+ cur-y (:gap cur-q))}
                  next-y (+ cur-y 
                            (* h 
                               (:pct cur-q)))   
                  lr {:x x1 :y next-y}]  
                (recur (conj result 
                             (assoc cur-q 
                                    :ul ul 
                                    :lr lr))
                       (rest q)
                       next-y)))))

(defn plot-block [y])
