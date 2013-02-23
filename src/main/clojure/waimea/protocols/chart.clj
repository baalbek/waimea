(ns waimea.protocols.chart)

(defprotocol IRuler
  (pix [this value] [this value f])
  (value [this pix]))
