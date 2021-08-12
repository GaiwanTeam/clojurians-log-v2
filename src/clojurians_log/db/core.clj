(ns clojurians-log.db.core
  (:require [integrant.core :as ig]
            [next.jdbc :as jdbc]
            [next.jdbc.date-time :as jdbc.date-time]))


(defmethod ig/init-key ::datasource [_ config]
  ;; TODO: add connection pooling
  (let [ds (jdbc/get-datasource config)]
    (jdbc.date-time/read-as-instant)
    (jdbc/with-options ds jdbc/unqualified-snake-kebab-opts)))

(defmethod ig/halt-key! ::datasource [_ datasource]
  ())
