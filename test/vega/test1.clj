(ns vega.test1
  (:use clojure.contrib.test-is
        vega.financial.calculator.testblackscholes))

(run-tests 'vega.financial.calculator.testblackscholes)

