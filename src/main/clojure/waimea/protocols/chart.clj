(ns waimea.protocols.chart)

(defprotocol IVruler
  (pixY [this value])
  (valY [this pix]))

(defprotocol IHruler
  (pixX [this value])
  (valX [this pix]))