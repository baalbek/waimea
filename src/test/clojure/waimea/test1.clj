(ns waimea.test1
    (:use clojure.contrib.test-is
          waimea.testehlers
          waimea.testutils))

(run-tests 'waimea.testehlers)
(run-tests 'waimea.testutils)

    
