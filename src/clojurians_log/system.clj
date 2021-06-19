(ns clojurians-log.system
  (:require [integrant.repl :as ig-repl]))

(defn get-config []
  {:org.oxal.clojurians-log.http/server {:port 8080}
   :org.oxal.clojurians-log.db.core/datasource {:dbtype "postgres"
                                                :user "myuser"
                                                :password "mypass"
                                                :dbname "clojurians_log"}})

(ig-repl/set-prep! get-config)

(def go ig-repl/go)
