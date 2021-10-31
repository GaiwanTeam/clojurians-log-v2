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
  (let [profile (fn [{:keys [default dev]}]
                  (condp = profile*
                    :dev dev
                    default))
        secret (fn [& in-keys]
                 (get-in (secrets) in-keys))]
    {:clojurians-log.http/server
     {:port 8000
      :ds (ig/ref :clojurians-log.db.core/datasource)}
     :clojurians-log.db.core/datasource
     {:dbtype "postgres"
      :user (profile {:default (secret :db :user) :dev "myuser"})
      :port (profile {:default 5432 :dev 54321})
      :password (profile {:default (secret :db :password) :dev "mypass"})
      :dbname "clojurians_log"
      :serverTimezone "UTC"}}))

(defn go [& [{:keys [profile]
              :or {profile :default}}]]
  (ig-repl/set-prep! #(doto (get-config profile) ig/load-namespaces))
  (ig-repl/go))
