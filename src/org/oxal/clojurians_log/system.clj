(ns org.oxal.clojurians-log.system
  (:require [integrant.repl :as ig-repl]))

(defn get-config []
  {:org.oxal.clojurians-log.http/server {:port 8080}})

(ig-repl/set-prep! get-config)

(def go ig-repl/go)
