(ns clojurians-log.db.queries
  (:require
   [honey.sql :as sql]
   [clojurians-log.db :as db]
   [next.jdbc :as jdbc]
   [next.jdbc.result-set :as rs]))

(defn all-messages []
  (let [sqlmap {:select [:message.* :member.*]
                :from [:message]
                :join [:member [:= :message.member-id :member.id]] }
        query (sql/format sqlmap)
        data (db/execute! query {:builder-fn rs/as-kebab-maps})]
    data))

(defn single-message [channel-id ts]
  (let [sqlmap {:select [:message.*]
                :from [:message]
                :limit 1
                :where [:and
                        [:= :channel-id channel-id]
                        [:= :ts ts]]}
        query (sql/format sqlmap)
        data (db/execute! query {:builder-fn rs/as-kebab-maps})]
    (first data)))

(defn messages-by-channel-date [channel-id date]
  (let [sqlmap {:select [:message.* :member.*]
                :from [:message]
                :where [:and
                        [:is :parent nil]
                        [:= :message.channel-id channel-id]
                        [:= [[:cast :message.created-at :DATE]] [:cast date :DATE]]]
                ;; TODO: should sort based on ts instead of id
                :order-by [:message.id]
                :join [:member [:= :message.member-id :member.id]]
                }
        query (sql/format sqlmap)
        data (db/execute! query {:builder-fn rs/as-kebab-maps})]
    data))

(defn replies-for-messages [channel-id message-ids]
  (let [sqlmap {:select [:message.* :member.*]
                :from [:message]
                :where [:and
                        [:= :message.channel-id channel-id]
                        [:in :message.parent message-ids]]
                ;; TODO: should sort based on ts instead of id
                :order-by [:message.id]
                :join [:member [:= :message.member-id :member.id]]
                }
        query (sql/format sqlmap)
        data (db/execute! query {:builder-fn rs/as-kebab-maps})]
    data))

(defn reactions-for-messages [channel-id message-ids]
  (let [sqlmap {:select [[[:count :reaction.*]] :reaction.reaction :reaction.message-id]
                :from [:reaction]
                :where [:and
                        [:= :reaction.channel-id channel-id]
                        [:in :reaction.message-id message-ids]]
                ;; TODO: should sort based on ts instead of id
                :order-by [:reaction.message-id]
                :group-by [:reaction.message-id :reaction.reaction]
                }
        query (sql/format sqlmap)
        data (db/execute! query {:builder-fn rs/as-kebab-maps})]
    data))

(defn channel-by-name [channel-name]
  (let [sqlmap {:select [:*]
                :from [:channel]
                :where [:= :name channel-name]}
        query (sql/format sqlmap)
        data (db/execute! query)]
    (first data)))

(defn channel-message-counts-by-date [channel-id]
  (let [sqlmap {:select [[[:count :*]]
                         [[:cast :created-at :date]]]
                :from [:message]
                :limit 300
                :where [:and
                        [:= :channel-id channel-id]
                        [:<> :created-at nil]]
                :order-by [[:created-at :desc]]
                :group-by [[:cast :created-at :date]]}
        data (db/execute! (sql/format sqlmap))]
    data))

(defn all-channels []
  (let [sqlmap {:select [:channel.*]
                :from [:channel]
                :order-by [:channel.name]}
        data (db/execute! (sql/format sqlmap))]
    data))

(defn member-cache-id-name []
  (let [sqlmap {:select [:slack-id :name]
                :from [:member]}
        data (db/execute! (sql/format sqlmap))]
    (into {}
          (map (juxt :slack-id :name))
          data)))

(defn search-messages [search-query]
  (let [sqlmap {:select [:message.* :member.* [[:over [[:count :*]]] :full-count]]
                :from [:message]
                :limit 200
                :join [:member [:= :message.member-id :member.id]]
                :order-by [[:created-at :desc]]
                :where [[:raw ["to_tsvector('english', text) @@ websearch_to_tsquery('english'," [:param :search-query] ")"]]]}
        query (sql/format sqlmap {:params {:search-query search-query}})
        data (db/execute! query {:builder-fn rs/as-kebab-maps})]
    data))

(defn chan-cache []
  (let [sqlmap {:select [:id :name]
                :from [:channel]}
        data (db/execute! (sql/format sqlmap))]
    (into {}
          (map (juxt :name :id))
          data)))

(defn chan-slack-id->id-cache []
  (let [sqlmap {:select [:id :slack-id]
                :from [:channel]}
        data (db/execute! (sql/format sqlmap))]
    (into {}
          (map (juxt :slack-id :id))
          data)))

(defn member-cache []
  (let [sqlmap {:select [:id :slack-id]
                :from [:member]}
        data (db/execute! (sql/format sqlmap))]
    (into {}
          (map (juxt :slack-id :id))
          data)))

(defn get-cache []
  {:chan-name->id (chan-cache)
   :chan-slack-id->id (chan-slack-id->id-cache)
   :member-slack->db-id (member-cache)})

(comment
  (def ds (user/ds))

  (single-message ds 2 "1652821554.591519")

  (replies-for-messages ds 79 [619])

  (reactions-for-messages ds 552 [227916])

  (member-cache-id-name ds)

  (take 10
        (all-messages ds))
  )
