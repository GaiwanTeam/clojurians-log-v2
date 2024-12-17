(ns clojurians-log.db.slack-import
  (:require
   [camel-snake-kebab.core :as csk]
   [camel-snake-kebab.extras :as cske]
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [clojurians-log.db :as db]
   [clojurians-log.db.import :as import]
   [clojurians-log.db.queries :as queries]
   [honey.sql :as sql]
   [next.jdbc :as jdbc]))

(defmulti from-event
  "Expects slack socket live events as maps in kebab case keywords"
  (fn [event cache]
    [(:type event) (:subtype event)]))

(defmethod from-event ["message" nil]
  [event cache]
  (let [query (-> event
                  (import/event->tx cache))
        sql-query (sql/format query)]
    #_(println sql-query)
    (db/execute! sql-query)))

(defmethod from-event ["message" "message_changed"]
  [event cache]
  (-> (:message event)
      (assoc :channel (:channel event))
      (from-event cache)))

(defmethod from-event ["message" "message_deleted"]
  [event cache]
  (let [query (-> event
                  (import/message-deleted->tx cache))
        sql-query (sql/format query)]
    (db/execute! sql-query)))

(defmethod from-event ["message" "tombstone"]
  [event cache]
  (println "TOOOOOMB")
  (println event)
  (let [query (-> event
                  (import/message-tombstone->tx cache))
        sql-query (sql/format query)]
    (db/execute! sql-query)))

(defmethod from-event ["reaction_added" nil]
  [event cache]
  (let [query (-> event
                  (import/reaction->tx cache))]
    (db/execute! (sql/format query))))

(defmethod from-event ["reaction_removed" nil]
  [event cache]
  (let [query (-> event
                  (import/reaction-removed->tx cache))]
    (db/execute! (sql/format query))))

(defmethod from-event ["channel_created" nil]
  [event cache]
  (let [query (-> event
                  (import/channel->tx cache))]
    (db/execute! (sql/format query))))

(defmethod from-event ["channel_rename" nil]
  [event cache]
  (-> event
      (assoc :type "channel_created")
      (from-event cache)))

(defmethod from-event :default
  [event cache]
  (println "Event import not caught of type: " (:type event)))

(defn from-file [f & {:keys [is-json?]}]
  (let [read-func (if is-json? json/read-str read-string)]
    (with-open [reader (io/reader f)]
      (doseq [line (line-seq reader)]
        (let [event (->> line
                         read-func
                         (cske/transform-keys csk/->kebab-case-keyword))]
          (from-event event (queries/get-cache)))))))

(defn from-dir [dir & {:keys [is-json?] :as opts}]
  (let [dir (io/file dir)
        files (->> dir
                   file-seq
                   (filter #(.isFile %))
                   sort)]
    (doseq [f files]
      (from-file f opts))))

(comment
  (def ds (user/ds))
  (from-file "/tmp/2022-05-17.edn" )
  (from-file "/Users/ox/Downloads/does-slack-archive/2021-05-01.txt" {:is-json? true})
  (json/read-str "{\"hello\": \"world\"}")
  )
