(ns clojurians-log.db.bulk-import
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [honey.sql :as sql]
            [clojurians-log.utils :as utils]
            [clojure.walk :as w]
            [next.jdbc :as jdbc]
            [integrant.repl.state :as ig-state]
            [clojure.set :as set]))

(defn read-json-from-file [path]
  (-> path
      io/resource
      slurp
      (json/read-str :key-fn keyword)))

(def ds (:clojurians-log.db.core/datasource ig-state/system))

(defn channels
  "Imports channels idempotently based on the slack_id"
  []
  (let [data (read-json-from-file "sample_data/channels.json")
        data (into []
                   (comp
                    (map #(utils/select-keys-nested-as % [:id
                                                          :name
                                                          {:keys [:topic :value]
                                                           :rename :topic}
                                                          {:keys [:purpose :value]
                                                           :rename :purpose}]))
                    (map #(set/rename-keys % {:id :slack-id})))
                   data)
        sqlmap {:insert-into [:channel]
                :values data
                :on-conflict :slack-id
                :do-update-set {:fields [:name :topic :purpose]}}]
    (jdbc/execute! ds (sql/format sqlmap))))

(channels)
