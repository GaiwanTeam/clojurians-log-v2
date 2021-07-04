(ns clojurians-log.db.bulk-import
  (:require [clojure.java.io :as io]
            [clojurians-log.db.import :as import]
            [clojurians-log.utils :as utils]
            [honey.sql :as sql]
            [integrant.repl.state :as ig-state]
            [next.jdbc :as jdbc]))

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

(defn members
  "Imports members idempotently based on the (slack) id"
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
                              ;;[:profile :image-original]
                              [:profile :image-24]
                              [:profile :image-32]
                              [:profile :image-48]
                              [:profile :image-72]
                              [:profile :image-192]
                              [:profile :image-512]
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
        sqlmap {:insert-into [:member]
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
                                         :image-24
                                         :image-32
                                         :image-48
                                         :image-72
                                         :image-192
                                         :image-512
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


(defn messages
  "Imports messages idempotently based on the slack_id"
  [ds channel cache]
  (let [channel-dir (io/file (str "src/sample_data/" channel))
        msg-files (file-seq channel-dir)]
    (doseq [msg-file msg-files]
      (when (.isFile msg-file)
        (println "Importing from file:" (.getAbsolutePath msg-file))
        (let [data (utils/read-json-from-file msg-file)
              data (into []
                         (comp
                          (map #(utils/select-keys-nested-as
                                 % [:text
                                    :type
                                    :subtype
                                    :purpose
                                    :user
                                    :ts
                                    :thread-ts]))
                          (map #(assoc %
                                       :channel-id
                                       (get-in cache [:chan-name->id channel]))))
                         data)
              sqlvals (remove nil? (mapv #(import/event->tx % cache) data))
              sqlmap {:insert-into [:message]
                      :values sqlvals
                      ;;:on-conflict []
                      :on-conflict {:on-constraint :message_channel_id_ts_key}
                      :do-update-set {:fields [:text :channel-id]}
                      ;;:do-nothing true
                      }
              ]
          (when (seq sqlvals)
            (jdbc/execute! ds (sql/format sqlmap))))))))

(defn chan-cache [ds]
  (let [sqlmap {:select [:id :name]
                :from [:channel]}
        data (jdbc/execute! ds (sql/format sqlmap))]
    (into {}
          (map (juxt :name :id))
          data)))

(defn member-cache [ds]
  (let [sqlmap {:select [:id :slack-id]
                :from [:member]}
        data (jdbc/execute! ds (sql/format sqlmap))]
    (into {}
          (map (juxt :slack-id :id))
          data)))

(comment
  (do
    ;; eval buffer then eval this do form to populate db
    ;; make sure slack archive is stored in src/sample_data
    (defn exec [sqlmap]
      (println (sql/format sqlmap))
      (jdbc/execute! ds (sql/format sqlmap)))

    (defn get-cache []
      {:chan-name->id (chan-cache ds)
       :member-slack->db-id (member-cache ds)})

    (channels ds)
    (members ds)
    (messages ds "general" (get-cache)))

  ,)
