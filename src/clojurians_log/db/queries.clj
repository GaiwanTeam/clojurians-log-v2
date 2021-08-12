(ns clojurians-log.db.queries
  (:require [next.jdbc :as jdbc]
            [integrant.repl.state :as ig-state]
            [honey.sql :as sql]))

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

(channel-by-name ds "4clojure")

(defn channel-message-counts-by-date [ds channel-id]
  (let [sqlmap {:select [[[:count :*]]
                         [[:cast :created-at :date]]]
                :from [:message]
                :limit 100
                :where [:and
                        [:= :channel-id channel-id]
                        [:<> :created-at nil]]
                :group-by [[:cast :created-at :date]]}
        data (jdbc/execute! ds (sql/format sqlmap))]
    data))

(defn all-channels [ds]
  (let [sqlmap {:select [:channel.*]
                :from [:channel]
                :order-by [:channel.name]}
        data (jdbc/execute! ds (sql/format sqlmap))]
    data))

(comment
  (def ds (:clojurians-log.db.core/datasource ig-state/system))

  (take 10
        (all-messages ds))
  )

