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

(defn channel-message-counts-by-date [ds channel-name]
  (let [sqlmap {:select [[[:count :*]]
                         [[:cast :created-at :date]]]
                :from [:message]
                :limit 100
                :where [:and
                        [:= :channel-id 5]
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

