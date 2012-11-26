(ns waimea.blocks.block
    (:require 
        [waimea.rulers.vruler :as VR]
        [waimea.utils.commonutils :as U]
        [waimea.common :as C]))

(defn foundation [{:keys [
                            data-min
                            data-max
                            pct
                            start-date
                            end-date
                            gap
                            legend
                            vr-plot-fn
                            vr-segs
                            plotters]
                  :or {     pct 0.25
                            gap 20
                            legend :false
                            vr-plot-fn VR/plot-vruler
                            vr-segs 10
                            plotters nil}}]
  {:vruler
    {:min data-min
    :max data-max
    :color (:bg C/colors)
    :plot-fn vr-plot-fn
    :segs vr-segs}
  :hruler
    {:start (U/date-add-days start-date -10)
    :end (U/date-add-days end-date 20)
    :color (:bg C/colors)
    :legend legend}
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

