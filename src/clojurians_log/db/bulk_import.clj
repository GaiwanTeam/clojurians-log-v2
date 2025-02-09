(ns clojurians-log.db.bulk-import
  (:require
   [clojure.java.io :as io]
   [clojurians-log.config :as config]
   [clojurians-log.db :as db]
   [clojurians-log.db.import :as import]
   [clojurians-log.db.queries :as queries]
   [clojurians-log.slack.api :as slack-api]
   [clojurians-log.system :as system]
   [clojurians-log.utils :as utils]
   [honey.sql :as sql]
   [next.jdbc :as jdbc]))

(defn insert-channels!
  "Inserts channels into db idempotently based on the slack_id"
  [path-or-data]
  (cond
    (instance? java.io.File path-or-data)
    (insert-channels! (utils/read-json-from-file path-or-data))
    (string? path-or-data)
    (insert-channels! (utils/read-json-from-file (io/file path-or-data "channels.json")))
    (seq path-or-data)
    (let [slack-data path-or-data
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
                     slack-data)
          sqlmap {:insert-into [:channel]
                  :values data
                  :on-conflict :slack-id
                  :do-update-set {:fields [:name :topic :purpose]}
                  :returning [:id]}]
      (db/execute! (sql/format sqlmap)))))

(defn members
  "Imports members idempotently based on the (slack) id"
  [path-or-data]
  (cond
    (instance? java.io.File path-or-data)
    (doseq [members-chunk (partition-all 100 (utils/read-json-from-file path-or-data))]
      (members members-chunk))
    (string? path-or-data)
    (members (utils/read-json-from-file (io/file path-or-data "users.json")))
    (seq path-or-data)
    (let [slack-data path-or-data
          ;; TODO: use member->tx here
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
                     slack-data)
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
                                           :is-email-confirmed]}
                  :returning [:slack-id :name]}
          sqlquery (sql/format sqlmap)]
      (db/execute! sqlquery))))

(defn messages
  "Imports messages idempotently based on the slack_id"
  [path channel cache]
  (let [start# (. System (nanoTime))
        channel-dir (io/file path channel)
        msg-files (sort (file-seq channel-dir))
        file-count (atom 0)]
    (doseq [msg-file msg-files]
      (when (.isFile msg-file)
        (swap! file-count inc)
        (let [data (utils/read-json-from-file msg-file)
              data (into []
                         (comp
                          (map #(utils/select-keys-nested-as
                                 % [:text
                                    :type
                                    :subtype
                                    :purpose
                                    :user
                                    :reactions
                                    :ts
                                    :thread-ts]))
                          (map #(assoc %
                                       :channel-id
                                       (get-in cache [:chan-name->id channel]))))
                         data)
              message-vals (remove nil? (mapv #(import/event->tx % cache) data))
              message-query {:insert-into [:message]
                             :values (map :values message-vals)
                             ;;:on-conflict []
                             :on-conflict {:on-constraint :message_channel_id_ts_key}
                             :do-update-set {:fields [:text :channel-id]}
                             ;;:do-nothing true
                             :returning [:ts :id]
                             }]
          (when (seq message-vals)
            (let [inserted-messages (db/execute! (sql/format message-query))
                  cache (assoc cache :message-ts->db-id (into {}
                                                              (map (juxt :ts :id))
                                                              inserted-messages))
                  reaction-vals (remove nil? (mapcat #(import/reactions->tx % cache)
                                                     (filter :reactions data)))
                  reaction-query {:insert-into [:reaction]
                                  :values reaction-vals}]
              (when (seq reaction-vals)
                (db/execute! (sql/format reaction-query))))))))
    (println (format "%4d files [%10.2f s] <- %s"
                     @file-count
                     (/ (double (- (. System (nanoTime)) start#)) 1000000000.0)
                     channel))))

(defn log-bot-channels []
  (let [slack-conn (slack-api/conn (config/get :slack-socket/bot-token))
        bot-chans (slack-api/get-users-conversations
                   slack-conn
                   {:user (config/get :slack/bot-user)})]
    (mapv :name bot-chans)))

(defn member-import-from-api!
  "Imports members from the Slack API."
  [slack-conn]
  (let [slack-users (slack-api/get-users slack-conn)]
    (doall
     (mapcat #(members %) (partition-all 100 slack-users)))
    #_(doall
       (apply
        concat
        (for [p-users (partition-all 100 slack-users)]
          (members p-users))))))

(defn channel-import-from-api!
  "Imports channels from the Slack API."
  [slack-conn]
  (let [slack-channels (slack-api/get-channels slack-conn)
        allowed-chans (set (log-bot-channels))
        slack-channels-to-log (filter #(contains? allowed-chans (:name %)) slack-channels)]
    (insert-channels! slack-channels-to-log)))

(defn channel-member-import
  "Import channel data and member data from path of slack data directory.
  If path is nil, imports from the slack api."
  [& [{:keys [path]
       :or {path nil}}]]
  (let [slack-conn (slack-api/conn (config/get :slack-socket/bot-token))
        chan-file (io/file path "channels.json")
        members-file (io/file path "users.json")
        imported-channels
        (if (.exists chan-file)
          ;; TODO: filter allowed channels for file import too
          (insert-channels! chan-file)
          (channel-import-from-api! slack-conn))
        imported-members-list
        (if (.exists members-file)
          (members members-file)
          (member-import-from-api! slack-conn)
          )
        stats {:imported-channels-count
               (-> imported-channels first :next.jdbc/update-count)
               :imported-members-count
               (apply + (map #(-> % first :next.jdbc/update-count) imported-members-list))}]
    (println stats)
    stats))

(defn messages-all [path]
  (for [chan (queries/all-channels)]
    (messages path (:name chan) (queries/get-cache))))

(comment
  ;; eval buffer then eval this do form to populate db
  ;; make sure slack archive is stored in src/sample_data
  (defn exec [sqlmap]
    (println (sql/format sqlmap))
    (db/execute! (sql/format sqlmap)))

  (def slack-conn (slack-api/conn (config/get :slack-socket/bot-token)))
  slack-conn

  (channel-member-import "../slack_exports/2025-03-01--2024-12-14/")
  (log-bot-channels)

  (def path "../clojurians-log-data/sample_data")

  (messages path "announcements" (queries/get-cache))

  (in-ns 'clojurians-log.db.bulk-import)
  (use 'clojurians-log.db.bulk-import)
  (messages "/data/2021-10-31" "announcements" (queries/get-cache))

  (messages-all path)

  (let [query {:insert-into [:reaction]
               :values [{:channel-id 1
                         :member-id 1829
                         :message-id 23515}]
               #_#_:on-conflict {}
               :returning [:channel-id :id]}]
    (db/execute! (sql/format query)))

  (let [query {:delete-from [:reaction]
               :returning [:id]}]
    (db/execute! (sql/format query)))

  (let [query {:select [:*]
               :from [:reaction]
               :limit 5}]
    (db/execute! (sql/format query {:return-keys true})))

  (let [query {:select [[[:count :*]]]
               :from [:reaction]}]
    (db/execute! (sql/format query {:return-keys true})))

  (let [query #_{:insert-into [:reaction]
                 :values [{:channel-id 1
                           :member-id 1829
                           :message-id 22160}]}
        {:select [:*]
         :from [:message]
         :where [:and
                 [:= :member-id 1829]
                 [:= :channel-id 1]]
         :limit 2}]
    (db/execute! (sql/format query {:return-keys true})))

  ,)
