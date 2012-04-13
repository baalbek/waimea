(ns waimea.common
    (:import 
        (java.awt Color BasicStroke)))

(def colors 
            {
             :itrend-200 Color/RED
             :itrend-50 Color/ORANGE
             :itrend-10 Color/WHITE
             :bg (Color. 80 80 80)
             :fg (Color. 148 148 24)
             :cndl-bear (Color. 175 14 14)
            })

(def strokes
            {
                :lineplot (BasicStroke. 0.5)
            })