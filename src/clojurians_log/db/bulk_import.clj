(ns clojurians-log.db.bulk-import
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojurians-log.utils :as utils]
            [honey.sql :as sql]
            [clojurians-log.utils :as utils]
            [clojure.walk :as w]
            [next.jdbc :as jdbc]
            [integrant.repl.state :as ig-state]
            [clojure.set :as set]))

(def ds (:clojurians-log.db.core/datasource ig-state/system))

(defn channels
  "Imports channels idempotently based on the slack_id"
  [ds]
  (let [data (utils/read-json-from-file "sample_data/channels.json")
        data (into []
                   (comp
                    (map #(utils/select-keys-nested-as
                           % [{:keys :id
                               :rename :slack-id}
                              :name
                              {:keys [:topic :value]
                               :rename :topic}
                              {:keys [:purpose :value]
                               :rename :purpose}])))
                   data)
        sqlmap {:insert-into [:channel]
                :values data
                :on-conflict :slack-id
                :do-update-set {:fields [:name :topic :purpose]}}]
    (jdbc/execute! ds (sql/format sqlmap))))

(defn users
  "Imports users idempotently based on the (slack) id"
  [ds]
  (let [data (utils/read-json-from-file "sample_data/users.json")
        data (into []
                   (comp
                    (map #(utils/select-keys-nested-as
                           % [:name
                              {:keys [:id]
                               :rename :slack-id}
                              :team-id
                              [:profile :real-name]
                              [:profile :real-name-normalized]
                              [:profile :display-name]
                              [:profile :display-name-normalized]
                              [:profile :first-name]
                              [:profile :last-name]
                              [:profile :title]
                              [:profile :skype]
                              [:profile :phone]
                              :is-admin
                              :is-bot
                              :tz
                              :tz-offset
                              :tz-label
                              :deleted
                              :bot-id
                              :is-email-confirmed])))
                   data)
        _ (println (first data))
        sqlmap {:insert-into ["user"]
                :values data
                :on-conflict :slack-id
                :do-update-set {:fields [:name
                                         :team-id
                                         :real-name
                                         :real-name-normalized
                                         :display-name
                                         :display-name-normalized
                                         :first-name
                                         :last-name
                                         :title
                                         :skype
                                         :phone
                                         :is-admin
                                         :is-bot
                                         :tz
                                         :tz-offset
                                         :tz-label
                                         :deleted
                                         :bot-id
                                         :is-email-confirmed]}}
        sqlquery (sql/format sqlmap)
        _ (println sqlquery)]
    (jdbc/execute! ds sqlquery)))
