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

(defn get-config []
  {:clojurians-log.http/server {:port 8000
                                :ds (ig/ref :clojurians-log.db.core/datasource)}
   #_#_:clojurians-log.http/css {}
   :clojurians-log.db.core/datasource {:dbtype "postgres"
                                       :user "myuser"
                                       :port 54321
                                       :password "mypass"
                                       :dbname "clojurians_log"
                                       :serverTimezone "UTC"}})

(ig-repl/set-prep! #(doto (get-config) ig/load-namespaces))

(def go ig-repl/go)
