(ns maunaloa.views.charts.blocks
  (:import
    [javafx.scene.paint Color]
    [oahux.repository ColorRepository ColorReposEnum]
    [vega.filters.ehlers Itrend CyberCycle])
  (:require
    [maunaloa.views.viewscommon :as VC]
    [waimea.utils.commonutils :as U]
    [maunaloa.utils.commonutils :as MU]
    (waimea.plotters
      [lineplotter :as LP]
      [candlestickplotter :as CNDL])
    [waimea.rulers.vruler :as VR]
    (waimea.blocks
      [block :as B])))

(defn drop-take [a b]
  (comp (partial take b) (partial drop a)))

(defn itrend-block [{:keys [^ColorRepository color-factory
                            bundle
                            num-items
                            skip-items
                            pct
                            padding
                            snap-unit
                            legend]
                     :or {
                           num-items 90
                           skip-items 0
                           padding 1.025
                           snap-unit nil
                           legend false
                           }
                     }]
  (let [dp (drop-take skip-items num-items)
        itrends (map #(dp (rseq %)) (:itrends bundle))
        data-values (dp (rseq (:prices bundle)))
        data-dx (dp (rseq (:dx bundle)))
        beans (dp (rseq (:beans bundle)))
        hi (MU/j1 .getHi beans)
        lo (MU/j1 .getLo beans)
        cndl-plotter (CNDL/candlestick-plotter beans 
                                               (.colorFor color-factory ColorReposEnum/FOREGROUND)
                                               (.colorFor color-factory ColorReposEnum/CNDL_BEAR)) 
        [data-min data-max] (U/find-min-max (conj (conj itrends hi) lo))
        itrend-plotters (map #(LP/single-line-plotter {:values %1
                                                       :dx data-dx
                                                       :stroke (.colorForCycle color-factory %2) ;(VC/get-color :itrend %2)
                                                       :line-width (VC/get-lw "itrend" %2)})
                          itrends (:freqs bundle))
        ]
   (B/foundation
       {:data-min (/ data-min padding)
        :data-max (* data-max padding)
        :start-date (last data-dx)
        :end-date (first data-dx)
        :pct pct
        :legend legend
        :plotters (conj itrend-plotters cndl-plotter)
        :snap-unit snap-unit
        })))

(defn cybercycle-block [{:keys [^ColorRepository color-factory
                                bundle
                                num-items
                                skip-items
                                pct
                                legend]
                         :or {
                              legend false
                              }}]
  (let [dp (drop-take skip-items num-items)
        cc (map #(dp (rseq %)) (:cc bundle))
        data-dx (dp (rseq (:dx bundle)))
        [data-min data-max] (U/find-min-max cc)
        cc-plotters (map #(LP/single-line-plotter {:values %1
                                                   :dx data-dx
                                                   :stroke (.colorForCycle color-factory %2) ;(VC/get-color :itrend %2)
                                                   :line-width (VC/get-lw "itrend" %2)})
                      cc (:freqs bundle))]
    (B/foundation
      {
        :data-min data-min
        :data-max data-max
        :start-date (last data-dx)
        :end-date (first data-dx)
        :vr-segs 4
        :pct pct
        :legend legend
        :plotters cc-plotters})))



(defn volume-block [{:keys [^ColorRepository color-factory
                            bundle
                            num-items
                            skip-items
                            pct
                            legend]
                         :or {
                              legend false
                              }}]
  (let [dp (drop-take skip-items num-items)
        data-dx (dp (rseq (:dx bundle)))
        volume (dp (rseq (:volume bundle)))
        plotters [(LP/volume-plotter volume data-dx 
                                     (.colorFor color-factory ColorReposEnum/VOLUME)
                                     )]]
    (B/foundation
      {
        :data-min 0.0
        :data-max (apply max volume)
        :start-date (last data-dx)
        :end-date (first data-dx)
        :vr-segs 5
        :pct pct
        ;:vr-plot-fn VR/plot-vol-ruler
        :legend legend
        :plotters plotters})))


