(ns clojurians-log.system
  (:require [integrant.core :as ig]
            [integrant.repl :as ig-repl]))

(defn get-config []
  {:clojurians-log.http/server {:port 8000}
   :clojurians-log.http/css {}
   :clojurians-log.db.core/datasource {:dbtype "postgres"
                                       :user "myuser"
                                       :password "mypass"
                                       :dbname "clojurians_log"}})

(ig-repl/set-prep! #(doto (get-config) ig/load-namespaces))

(def go ig-repl/go)
