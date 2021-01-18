(defproject confeus "0.2.0"
  :description "Confeus - Configuration file generator for Prometheus"
  :min-lein-version "2.0.0"
  :dependencies [[compojure "1.6.2"]
                 [http-kit "2.5.0"]
                 [org.clojure/clojure "1.10.1"]
                 [cheshire "5.10.0"]
                 [medley "1.3.0"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.clojure/tools.cli "0.4.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]]
  :plugins [[lein-ring "0.12.5"]]
  :ring {:handler confeus.core/app}
  :main confeus.core
  :uberjar-name "confeus.jar"
  :profiles
  {:dev {:dependencies [[ring/ring-mock "0.4.0"]]}
   :uberjar {:aot :all}})
