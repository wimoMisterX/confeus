(ns confeus.core
  (:gen-class)
  (:require [compojure.core :refer [defroutes PUT]]
            [compojure.route :as route]
            [clojure.tools.cli :as cli]
            [clojure.java.io :as io]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [cheshire.core :as json]
            [org.httpkit.server :as http]
            [medley.core :as m]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :as middleware]))

(defn str->json [s]
  (json/parse-string s true))

(defn json->str [s]
  (json/generate-string s {:pretty true}))

(defn import-job-config! [path]
  (->> path
       slurp 
       str->json
       (into {} (map (juxt :labels :targets)))))

(defn export-job-config! [path config]
  (->> config 
       (into [] (map #(hash-map :labels (first %) :targets (second %))))
       json->str
       (spit path)))

(defn- second-arg [_ arg2]
  arg2)

(defn update-config [update-action config {:keys [targets labels]}]
  (let [update-fn (condp = update-action 
                    :remove #(remove (set %2) %1)
                    :merge concat
                    :set second-arg)]
    (m/map-kv-vals (fn [k v]
                     (if (= k labels)
                       (->> (update-fn v targets)
                            distinct
                            sort)
                       (remove (set targets) v)))
                   (update config labels concat []))))

(defn apply-config-updates [config update-action updates]
  (reduce (partial update-config update-action) config updates))

(defn setup-config-file! [config-path job]
  (let [config-file (io/file config-path (str job ".json"))
        path (.getPath config-file) ]
    (when-not (.exists config-file)
      (io/make-parents path)
      (spit path "[]"))
    path))

(defroutes app-routes
  (PUT "/job/:job/config/:action{(merge|set|remove)}" [job action :as {updates :body config-path :config-path update-lock-atom :update-lock-atom}]
    (locking (-> update-lock-atom 
                 (swap! update job #(if (nil? %1) %2 %1) (Object.))
                 (get job))
      (let [config-file-path (setup-config-file! config-path job)
            config (import-job-config! config-file-path)
            updated-config (apply-config-updates config (keyword action) updates)]
        (if (= config updated-config)
          {:status 200
           :body {:message "Nothing changed"}}
          (do 
            (export-job-config! config-file-path updated-config)
            {:status 200 
             :body {:message "Config updated"}})))))
  (route/not-found "Oopsie...you're lost mate!"))

(defn wrap-config-path [f config-path update-lock-atom]
  (fn [req]
    (f (merge req {:config-path config-path
                   :update-lock-atom update-lock-atom}))))

(defn app-handler [config-path update-lock-atom]
  (-> app-routes
      (wrap-config-path config-path update-lock-atom)
      wrap-params
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :default 3000]
   [nil "--config-path CONFIG_PATH" "files_sd path"
    :default "/etc/prometheus/files_sd"]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [config-path port]} (:options (cli/parse-opts args cli-options))]
    (log/infof "Starting server on port %d" port)
    (http/run-server (app-handler config-path (atom {})) {:port port})))
