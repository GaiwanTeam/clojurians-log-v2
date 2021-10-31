(ns clojurians-log.db.queries
  (:require [next.jdbc :as jdbc]
            [integrant.repl.state :as ig-state]
            [honey.sql :as sql]))

(defn repl-ds
  "only for repl usage, don't use this directly"
  []
  (:clojurians-log.db.core/datasource ig-state/system))

(defn all-messages [ds]
  (let [sqlmap {:select [:message.* :member.*]
                :from [:message]
                :join [:member [:= :message.member-id :member.id]] }
        data (jdbc/execute! ds (sql/format sqlmap))]
    data))

(defn messages-by-channel-date [ds channel-id date]
  (let [sqlmap {:select [:message.* :member.*]
                :from [:message]
                :where [:and
                        [:= :message.channel-id channel-id]
                        [:= [[:cast :message.created-at :DATE]] [:cast date :DATE]]]
                :order-by [:message.id]
                :join [:member [:= :message.member-id :member.id]] 
                }
        query (sql/format sqlmap)
        _ (println query)
        data (jdbc/execute! ds query)]
    data))

(defn channel-by-name [ds channel-name]
  (let [sqlmap {:select [:*]
                :from [:channel]
                :where [:= :name channel-name]}
        query (sql/format sqlmap)
        data (jdbc/execute! ds query)]
    (first data)))

(defn channel-message-counts-by-date [ds channel-id]
  (let [sqlmap {:select [[[:count :*]]
                         [[:cast :created-at :date]]]
                :from [:message]
                :limit 300
                :where [:and
                        [:= :channel-id channel-id]
                        [:<> :created-at nil]]
                :order-by [[:created-at :desc]]
                :group-by [[:cast :created-at :date]]}
        data (jdbc/execute! ds (sql/format sqlmap))]
    data))

(defn all-channels [ds]
  (let [sqlmap {:select [:channel.*]
                :from [:channel]
                :order-by [:channel.name]}
        data (jdbc/execute! ds (sql/format sqlmap))]
    data))

(defn member-cache-id-name [ds]
  (let [sqlmap {:select [:slack-id :name]
                :from [:member]}
        data (jdbc/execute! ds (sql/format sqlmap))]
    (into {}
          (map (juxt :slack-id :name))
          data)))

(defn search-messages [ds search-query]
  (let [sqlmap {:select [:message.* :member.*]
                :from [:message]
                :join [:member [:= :message.member-id :member.id]]
                :where [[:raw ["to_tsvector('english', text) @@ websearch_to_tsquery('english'," [:param :search-query] ")"]]]}
        query (sql/format sqlmap {:params {:search-query search-query}})
        data (jdbc/execute! ds query)]
    data))

(comment
  (def ds (:clojurians-log.db.core/datasource ig-state/system))

  (member-cache-id-name ds)

  (take 10
        (all-messages ds))
  )

