(ns waimea.main
    (:require 
        [waimea.utils :as U]
        [waimea.blocks.block :as B]
        [waimea.blocks.quadrant :as Q])
    (:import (java.util Date Vector) 
             (java.awt Color Dimension)
             (javax.swing JFrame JLabel JTextField JButton JPanel JComponent)
             (javax.swing.event DocumentListener)
             (java.awt.event ActionListener)
             (java.awt Graphics Graphics2D RenderingHints GridLayout GridBagLayout GridBagConstraints Insets)))

(defn create-plot [qs mleft mtop mright mbtm]
    (proxy [JComponent] []  
        (paintComponent[g]
            (proxy-super paintComponent g)
            (let [w (.getWidth this)
                  h (.getHeight this)]
                (.setColor g Color/BLACK)
                (.fillRect g mleft 
                            mtop 
                            (- w (+ mleft mright)) 
                            (- h (+ mtop mbtm)))
                (let [qsx (B/block-chain 
                            :qs qs
                            :h (- h mtop mbtm)
                            :x0 mleft
                            :x1 (- w mright)
                            :y0 mtop)]
                    (doseq [q qsx]
                        (Q/plot-quadrant q g)))))))

(defn -main [& args]
    (def f (JFrame. "Waimea"))
    (.setDefaultCloseOperation f JFrame/EXIT_ON_CLOSE)
    (.setSize f (Dimension. 1500 1000)) 
    (let [d0 (U/parse-date "2011-1-1") 
          d1 (U/parse-date "2012-3-1")]
        (.add f (create-plot 
                        [
                        (B/create-block [20.0 100.0] :pct 0.3 :start-date d0 :end-date d1)
                        (B/create-block [50.0 200.0] :pct 0.2 :start-date d0 :end-date d1)
                        (B/create-block [120.0 800.0] :pct 0.5 :start-date d0 :end-date d1 :legend :true)
                        ]
                        60 30 30 30)))
    (.setVisible f true))


;(-main)
