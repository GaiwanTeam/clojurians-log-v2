(ns clojurians-log.db.core
  (:require [integrant.core :as ig]
            [next.jdbc :as jdbc]))

(defmethod ig/init-key ::datasource [_ config]
  ;; TODO: add connection pooling
  (let [ds (jdbc/get-datasource config)]
    (jdbc/with-options ds jdbc/unqualified-snake-kebab-opts)))

(defmethod ig/halt-key! ::datasource [_ datasource]
  ())
