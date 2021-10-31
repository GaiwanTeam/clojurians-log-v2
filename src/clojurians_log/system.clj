(ns clojurians-log.system
  (:require [integrant.core :as ig]
            [integrant.repl :as ig-repl]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn secrets []
  (-> "config/secrets.edn"
      io/resource
      slurp
      edn/read-string))

(defn get-config [profile*]
  (let [profile (fn [{:keys [prod dev]}]
                  (condp = profile*
                    :dev dev
                    prod))
        secret (fn [& in-keys]
                 (get-in (secrets) in-keys))]
    {:clojurians-log.http/server
     {:port 8000
      :ds (ig/ref :clojurians-log.db.core/datasource)}
     :clojurians-log.db.core/datasource
     {:dbtype "postgres"
      :user (profile {:prod (secret :db :user) :dev "myuser"})
      :port (profile {:prod 5432 :dev 54321})
      :password (profile {:prod (secret :db :password) :dev "mypass"})
      :dbname "clojurians_log"
      :serverTimezone "UTC"}}))

(defn go [& [{:keys [profile]
              :or {profile :dev}}]]
  (println "Launching with profile " profile)
  (ig-repl/set-prep! #(doto (get-config profile) ig/load-namespaces))
  (ig-repl/go))
