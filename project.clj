(defproject waimea "5.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
    [org.clojure/clojure "1.7.0"]
    [org.clojure/math.numeric-tower "0.0.4"]
    [log4j/log4j "1.2.17"]
    [commons-logging/commons-logging "1.2"]
    ;[org.mongodb/mongo-java-driver "3.3.0"]
    [org.mongodb/mongo-java-driver "2.12.5"]
    [colt/colt "1.2.0"]
    ]
  :target-path "target"
  :java-test-paths ["test-java"]
  :aot :all
  :resource-paths [
			"../oahu/build/libs/oahu-5.3.1.jar"
			"../oahux/build/libs/oahux-5.3.1.jar"
			"../maunaloax/build/libs/maunaloax-5.3.1.jar"
			"../vega/build/libs/vega-5.3.0.jar"
			"../ranoraraku/build/libs/ranoraraku-5.3.5.jar"
                    ]
  :profiles {:uberjar {:aot :all}})
