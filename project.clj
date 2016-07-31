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
    ]
  :target-path "target"
  :java-test-paths ["test-java"]
  :aot :all
  :resource-paths [
			"/home/rcs/opt/java/oahu/build/libs/oahu-5.3.1.jar"
			"/home/rcs/opt/java/oahux/build/libs/oahux-5.3.1.jar"
			"/home/rcs/opt/java/maunaloax/build/libs/maunaloax-5.3.1.jar"
                    ]
  :profiles {:uberjar {:aot :all}})
