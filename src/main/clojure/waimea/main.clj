(ns waimea.main
    (:require 
        [waimea.blocks.block :as B])
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
                        (println q)))))))

(defn -main [& args]
    (def f (JFrame. "Waimea"))
    (.setSize f (Dimension. 800 500)) 
    (.add f (create-plot 
                    [(B/create-block [0.0 100.0] :gap 10)
                     (B/create-block [0.0 100.0] :gap 10)
                     (B/create-block [0.0 100.0] :gap 10)]
                     60 30 30 30))
    (.setVisible f true))


(-main)
