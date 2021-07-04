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

(defn message->tx [{:keys [team user text ts thread-ts] :as message}
                   {:keys [chan-slack->db-id user-slack->db-id] :as cache}]
  (let [user-id (get user-slack->db-id user)
        channel-id (get chan-slack->db-id team)
        parent-ts (if thread-ts
                    {:select [:id]
                     :from [:message]
                     :where [:and
                             [:= :ts thread-ts]
                             [:= :channel-id channel-id]]}
                    nil)]
    {:insert-into [:message]
     :values [{:channel-id channel-id
               :user-id user-id
               :text text
               :ts ts
               :parent parent-ts
               :deleted-ts nil}]}))

(defmethod event->tx ["message" nil] [message cache]
  (message->tx message cache))

(defmethod event->tx ["message" "message_deleted"] [{:keys [deleted_ts channel] :as message} cache]
  {}
  )

(defmethod event->tx ["message" "message_changed"] [{:keys [message channel]} cache]
  (event->tx (assoc message :channel channel)))

(defmethod event->tx ["message" "thread_broadcast"] [message cache]
  (assoc
   (message->tx message) :message/thread-broadcast? true))
