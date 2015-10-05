(defproject helpme "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/data.csv "0.1.2"]
                 [cheshire "5.5.0"]
                 [com.cognitect/transit-clj "0.8.281"]
                 [org.clojure/tools.logging "0.3.1"]
                 [log4j/log4j "1.2.17"]]
  :source-paths ["src/clj"]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]
                   :source-paths ["test"]}})
