(ns waimea.protocols.financial
  (:import
    [oahu.financial StockPrice]))

(defprotocol IStockprice
  (dx [this])
  (opn [this])
  (hi [this])
  (lo [this])
  (spot [this])
  (value [this])
  (volume [this]))

(extend-protocol IStockprice
  StockPrice
  (dx [this] (.getDx this))
  (opn [this] (.getOpn this))
  (hi [this] (.getHi this))
  (lo [this] (.getLo this))
  (spot [this] (.getCls this))
  (value [this] (.getValue this))
  (volume [this] (.getVolume this)))

