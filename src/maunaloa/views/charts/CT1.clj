(ns maunaloa.views.charts.CT1
  ( :gen-class
    :init init
    :state state
    :implements [oahux.chart.MaunaloaChart]
    :methods [
               [setIsWeekly [boolean] void]
               [setHasVolume [boolean] void]
               [setColorRepos [oahux.repository.ColorRepository] void]
               [getColorRepos [] oahux.repository.ColorRepository]
               ;[setColorBackground [javafx.scene.paint.Color] void]
               ;[setColorForeground [javafx.scene.paint.Color] void]
               ;[setColorItrend10 [javafx.scene.paint.Color] void]
               ;[setColorItrend50 [javafx.scene.paint.Color] void]
               ;[setColorItrend200 [javafx.scene.paint.Color] void]
               ;[setColorVolume [javafx.scene.paint.Color] void]
               ])
  (:import
    [oahux.controllers MaunaloaChartViewModel]
    [javafx.scene.paint Color]
    [javafx.scene.input MouseEvent]
    [javafx.scene.canvas Canvas GraphicsContext])
  (:require
    [maunaloa.utils.commonutils :as hx]
    (waimea.plotters
      [candlestickplotter :as CNDL])
    (waimea.blocks
      [block :as B]
      [quadrant :as Q])
    [maunaloa.views.viewscommon :as VC]
    [waimea.plotters.lineplotter :as LP]
    [maunaloa.utils.commonutils :as U]
    [maunaloa.views.charts.blocks :as CB]))



;;------------------------------------------------------------------------
;;---------------------------- Mouse Events ------------------------------
;;------------------------------------------------------------------------

;;------------------------------------------------------------------------
;;---------------------------- Clojure Methods ------------------------------
;;------------------------------------------------------------------------

(comment get-color [^maunaloa.views.charts.CT1 this category & [subcat]]
  (let [__state__ @(.state this)]
    (if (= category :itrend)
      (do
        (cond
          (= subcat 10)  (:coloritrend10 __state__)
          (= subcat 50)  (:coloritrend50 __state__)
          (= subcat 200) (:coloritrend200 __state__)))
      (category __state__))))


        

(defn create-vm-blox [^maunaloa.views.charts.CT1 this num-items skip-items]
  (let [vm ^MaunaloaChartViewModel (:viewmodel @(.state this))
        has-volume (:hasvolume @(.state this))
        bundle (VC/get-bundle this)
        ;color-factory (partial get-color this) 
        color-factory (.getColorRepos this) 
        itrend-block (CB/itrend-block  {:color-factory color-factory
                                        :bundle bundle
                                        :pct 0.5
                                        :num-items num-items
                                        :skip-items skip-items
                                        :snap-unit 1})
        cc-pct (if (= has-volume true) 0.25 0.5)
        cc-block (CB/cybercycle-block {:color-factory color-factory
                                       :bundle bundle
                                       :pct cc-pct
                                       :num-items num-items
                                       :skip-items skip-items })
        volume-block (if (= has-volume true)
                       (CB/volume-block {:color-factory color-factory
                                         :bundle bundle
                                       :pct 0.25
                                       :num-items num-items
                                       :skip-items skip-items
                                       :legend true})
                         nil)
        blocks (if (nil? volume-block)
                 [itrend-block cc-block]
                 [itrend-block cc-block volume-block])
        ]
    [vm blocks]))

;;------------------------------------------------------------------------
;;---------------------------- Java methods ------------------------------
;;------------------------------------------------------------------------
(defn -init []
  [[] (atom 
        {:skip (atom 0)
        })])

(hx/defprop :set "hasVolume")
(hx/defprop :set "isWeekly")
(hx/defprop :getset "colorRepos")

;;------------------------------------------------------------------------
;;-------------------------- Interface methods ---------------------------
;;------------------------------------------------------------------------
;(hx/defprop :getset "numShiftWeeks")
(hx/defprop :set "viewModel")

(defn -getLastCurrentDateShown [^maunaloa.views.charts.CT1 this]
  (let [bundle (VC/get-bundle this)
        cur-skip  @(:skip @(.state this))
        dx ^java.sql.Date (nth (rseq (:dx bundle)) cur-skip)]
    (.toLocalDate dx)))

(def -shiftWeeks (VC/shiftWeeks create-vm-blox 90))
(def -shiftDays (VC/shiftDays create-vm-blox 90))
(def -draw (VC/draw create-vm-blox 90))
(def -shiftToEnd (VC/shiftToEnd create-vm-blox 90))
(def -shiftToDate (VC/shiftToDate create-vm-blox 90))

;vol-block (CB/volume-block volume-items dx 0.25)


