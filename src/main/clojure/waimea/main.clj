(ns waimea.main
    (comment
    (:require  [taharoa.utils :as Utils]
               [taharoa.plotters :as P]
               [taharoa.vruler :as VR]
               [taharoa.dateruler :as DR]
               [taharoa.filters.ehlers :as Ehlers]
               [taharoa.filters.filterutils :as FilterUtils]
               [taharoa.saxmodel :as Saxmodel]))
    (:import (java.util Date Vector) 
             (java.awt Color)
             (javax.swing JFrame JLabel JTextField JButton JPanel JComponent)
             (javax.swing.event DocumentListener)
             (java.awt.event ActionListener)
             (java.awt Graphics Graphics2D RenderingHints GridLayout GridBagLayout GridBagConstraints Insets)))

(defn -main [& args]
    (println "Hi"))

(-main)
