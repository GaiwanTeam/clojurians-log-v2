(ns clojurians-log.routes
  (:require [clojurians-log.layout :as layout]
            [clojurians-log.components.common :as common]
            [clojurians-log.db.queries :as queries]
            [lambdaisland.ornament :as o]))

(defn handler [{:keys [ds] :as request}]
  (let [msgs (queries/all-messages ds)]
    {:status 200
     :body {:channels (queries/all-channels ds)
            :messages msgs}
     :view (fn [data]
             [layout/base
              [common/home-page data]])}))

(defn channel-handler [{:keys [ds path-params] :as request}]
  (let [channel (queries/channel-by-name ds (:channel path-params))
        channels (queries/all-channels ds)
        message-counts-by-date (queries/channel-message-counts-by-date ds (:id channel))]
    {:status 200
     :body {:channels channels
            :channel channel
            :message-counts-by-date message-counts-by-date}
     :view (fn [data]
             [layout/base
              [common/channel-page data]])}))

(defn channel-date-handler [{:keys [ds path-params] :as request}]
  (let [channel (queries/channel-by-name ds (:channel path-params))
        channels (queries/all-channels ds)
        messages (queries/messages-by-channel-date ds (:id channel) (:date path-params))
        message-counts-by-date (queries/channel-message-counts-by-date ds (:id channel))]
    {:status 200
     :body {:channels channels
            :channel channel
            :messages messages
            :date (:date path-params)
            :message-counts-by-date message-counts-by-date}
     :view (fn [data]
             [layout/base
              [common/channel-date-page data]])}))

(defn routes []
  [["/" {:get handler}]
   ["/about" {:get handler}]
   ["/sitemap" {:get handler}]
   ["/healthcheck" {:get handler}]
   ["/:channel" {:get channel-handler}]
   ["/:channel/:date" {:get channel-date-handler}]])
