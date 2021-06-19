(ns clojurians-log.db.bulk-import
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [integrant.repl.state :as ig-state]
            [clojure.set :as set]))

(defn read-json-from-file [path]
  (-> path
      io/resource
      slurp
      (json/read-str :key-fn keyword)))

(def ds (:clojurians-log.db.core/datasource ig-state/system))

(defn channels []
  (let [data (read-json-from-file "sample_data/channels.json")
        data (into []
                   (comp
                    (map #(select-keys % [:id :name]))
                    (map #(set/rename-keys % {:id :slack-id})))
                   data)
        sqlmap {:insert-into [:channel]
                :values data}]
    (jdbc/execute! ds (sql/format sqlmap))))
