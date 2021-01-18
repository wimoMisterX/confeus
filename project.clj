(defproject confeus "0.2.0"
  :description "Confeus - Configuration file generator for Prometheus"
  :min-lein-version "2.0.0"
  :dependencies [[compojure "1.6.1"]
                 [http-kit "2.3.0"]
                 [org.clojure/clojure "1.9.0"]
                 [cheshire "5.9.0"]
                 [medley "1.3.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.clojure/tools.cli "0.4.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.4.0"]]
  :plugins [[lein-ring "0.12.4"]]
  :ring {:handler confeus.core/app}
  :main confeus.core
  :uberjar-name "confeus.jar"
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}
   :uberjar {:aot :all}})
