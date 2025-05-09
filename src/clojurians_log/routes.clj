(ns clojurians-log.routes
  (:require
   [clojurians-log.layout :as layout]
   [clojurians-log.components.common :as common]
   [clojurians-log.db.queries :as queries]
   [clojurians-log.open-graph :as og]
   [lambdaisland.ornament :as o]))

(defn handler [request]
  (let [channels (queries/all-channels)]
    {:status 200
     :body   {:channels channels}
     :view   (fn [data]
               [layout/base
                (og/social-tags {})
                [common/home-page data]])}))

(defn channel-handler [{:keys [path-params] :as request}]
  (let [channel                (queries/channel-by-name (:channel path-params))
        channels               (queries/all-channels)
        message-counts-by-date (queries/channel-message-counts-by-date (:id channel))]
    {:status 200
     :body   {:channels               channels
              :channel                channel
              :message-counts-by-date message-counts-by-date}
     :view   (fn [data]
               [layout/base
                (og/social-tags {:title (str (-> data :channel :name) " | Clojure Slack Archive")})
                [common/channel-page data]])}))

(defn channel-date-handler [{:keys [path-params] :as request}]
  (let [channel                (queries/channel-by-name (:channel path-params))
        channels               (queries/all-channels)
        member-cache-id-name   (queries/member-cache-id-name)
        messages               (queries/messages-by-channel-date (:id channel) (:date path-params))
        messages-ids           (map :message/id messages)
        raw-replies            (queries/replies-for-messages (:id channel) messages-ids)
        raw-replies-ids        (map :message/id raw-replies)
        message-counts-by-date (queries/channel-message-counts-by-date (:id channel))
        reactions              (->> (queries/reactions-for-messages
                                     (:id channel)
                                     (concat messages-ids
                                             raw-replies-ids))
                                    (group-by :reaction/message-id))
        messages               (map #(assoc % :reactions (get reactions (:message/id %))) messages)
        raw-replies            (map #(assoc % :reactions (get reactions (:message/id %))) raw-replies)
        replies                (->> raw-replies
                                    (group-by :message/parent))]
    {:status 200
     :body   {:channels               channels
              :channel                channel
              :member-cache-id-name   member-cache-id-name
              :messages               messages
              :replies                replies
              :reactions              reactions
              :date                   (:date path-params)
              :message-counts-by-date message-counts-by-date}
     :view   (fn [data]
               [layout/base
                (og/social-tags {:title (str (-> data :date) " " (-> data :channel :name) " | Clojure Slack Archive")})
                [common/channel-date-page data]])}))

(defn search-handler [{:keys [query-params] :as req}]
  (let [query    (get query-params "q")
        messages (queries/search-messages query)]
    {:status 200
     :body   {:query    query
              :messages messages}
     :view   (fn [data]
               [layout/base
                (og/social-tags {:title (str "Search Clojure Slack Archive")})
                [common/search-page data]])}))

(defn routes []
  [["/" {:get handler}]
   ["/about" {:get handler}]
   ["/sitemap" {:get handler}]
   ["/healthcheck" {:get handler}]
   ["/search" {:get search-handler :parameter {:query {:q string?}}}]
   ["/:channel" {:get channel-handler}]
   ["/:channel/:date" {:get channel-date-handler}]])
