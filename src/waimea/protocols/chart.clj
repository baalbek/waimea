(ns waimea.protocols.chart)

(defprotocol IRuler
  (calcPix [this value] [this value f])
  (calcValue [this pix]))
