(ns clojurians-log.db.import
  (:require [honey.sql :as sql]))

(def default-cache
  {:channels {}
   :users {}})

(defmulti event->tx
  (fn [message cache]
    ((juxt :type :subtype) message)))

(defmethod event->tx :default [_ _]
  ;; return nil by default, this will let us skip events we don't (yet) care
  ;; about
  nil)

(defn message->tx [{:keys [channel-id user text ts thread-ts] :as message}
                   {:keys [user-slack->db-id] :as cache}]
  (println channel-id)
  (let [user-id (get user-slack->db-id user)
        parent-ts (if thread-ts
                    {:select [:id]
                     :from [:message]
                     :limit 1
                     :where [:and
                             [:= :ts thread-ts]
                             [:= :channel-id channel-id]]}
                    nil)]
    {:channel-id channel-id
     :user-id user-id
     :text text
     :ts ts
     :parent parent-ts
     :deleted-ts nil}))

(defmethod event->tx ["message" nil] [message cache]
  (message->tx message cache))

(defmethod event->tx ["message" "message_deleted"] [{:keys [deleted_ts channel] :as message} cache]
  {}
  )

(defmethod event->tx ["message" "message_changed"] [{:keys [message channel]} cache]
  (event->tx (assoc message :channel channel)))

(defmethod event->tx ["message" "thread_broadcast"] [message cache]
  {}
  #_(assoc
     (message->tx message) :message/thread-broadcast? true))
