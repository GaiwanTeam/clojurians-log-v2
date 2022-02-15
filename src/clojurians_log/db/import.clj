(ns clojurians-log.db.import
  (:require [honey.sql :as sql]
            [clojure.string :as string]
            [clojurians-log.time-utils :as time-utils]))

(defmulti event->tx
  (fn [message cache]
    ((juxt :type :subtype) message)))

(defmethod event->tx :default [_ _]
  ;; return nil by default, this will let us skip events we don't (yet) care
  ;; about
  nil)

(defn message->tx [{:keys [channel-id user text ts thread-ts] :as message}
                   {:keys [member-slack->db-id] :as cache}]
  (let [member-id (get member-slack->db-id user)
        parent-id (when (and thread-ts (not= thread-ts ts))
                    {:select [:id]
                     :from [:message]
                     :limit 1
                     :where [:and
                             [:= :ts thread-ts]
                             [:= :channel-id channel-id]]})]
    {:channel-id channel-id
     :member-id member-id
     :text (string/replace text #"\u0000" "")
     :ts ts
     :created-at (time-utils/ts->inst ts)
     :parent parent-id
     :deleted-ts nil}))

(defmethod event->tx ["message" nil] [message cache]
  (message->tx message cache))

(defmethod event->tx ["message" "message_deleted"] [{:keys [deleted_ts channel] :as message} cache]
  nil)

(defmethod event->tx ["message" "message_changed"] [{:keys [message channel]} cache]
  #_(event->tx (assoc message :channel channel)))

(defmethod event->tx ["message" "thread_broadcast"] [message cache]
  nil
  #_(assoc
     (message->tx message) :message/thread-broadcast? true))

(defn reaction->tx [{:keys [channel-id user reactions ts thread-ts] :as message}
                    {:keys [member-slack->db-id] :as cache}]
  (let [member-id (get member-slack->db-id user)
        reactions-with-user-ids (map (fn [reaction-entry]
                                       (update reaction-entry
                                               :users
                                               (fn update-user-slack-to-db-id [user-list]
                                                 (map #(get member-slack->db-id %) user-list))))
                                     reactions)]
    (map (fn reaction-val [reaction-entry]
           {:channel-id channel-id
            :member-id member-id
            :ts ts
            :message-id nil
            :reaction nil
            :url nil}
           )
         reactions-with-user-ids)
    )
  )
