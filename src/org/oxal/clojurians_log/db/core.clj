(ns org.oxal.clojurians-log.db.core
  (:require [next.jdbc :as jdbc]))

(defmethod ig/init-key ::datasource [_ config]
  ;; TODO: add connection pooling
  (let [ds (jdbc/get-datasource config)]
    ds))

(defmethod ig/halt-key! ::datasource [_ datasource]
  ())
