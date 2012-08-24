(ns waimea.protocols.financial
  (:import [rarotonga.financial Stock]))

(defprotocol IStockprice
  (dx [this])
  (opn [this])
  (hi [this])
  (lo [this])
  (spot [this])
  (value [this])
  (volume [this]))

(extend-protocol IStockprice
  Stock
  (dx [this] (.getDx this))
  (opn [this] (.getOpn this))
  (hi [this] (.getHi this))
  (lo [this] (.getLo this))
  (spot [this] (.getCls this))
  (value [this] (.getCls this))
  (volume [this] (.getVolume this)))

