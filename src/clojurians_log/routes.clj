(ns clojurians-log.routes
  (:require [clojurians-log.layout :as layout]
            [clojurians-log.components.common :as common]
            [clojurians-log.db.queries :as queries]
            [lambdaisland.ornament :as o]))

(defn handler [{:keys [ds] :as request}]
  (let [msgs (queries/all-messages ds)]
    {:status 200
     :view (fn [data]
             [layout/base
              [common/home-page data]])
     :body {:channels (queries/all-channels ds)
            :messages msgs}}))

(defn channel-handler [{:keys [ds path-params] :as request}]
  (let [msgs (queries/all-messages ds)
        channel-name (:channel path-params)
        channels (queries/all-channels ds)
        message-counts-by-date (queries/channel-message-counts-by-date ds 5)]
    {:status 200
     :view (fn [data]
             [layout/base
              [common/channel-page data]])
     :body {:channels channels
            :channel-name channel-name
            :message-counts-by-date message-counts-by-date
            :messages msgs}}))

(defn routes []
  [["/" {:get handler}]
   ["/about" {:get handler}]
   ["/sitemap" {:get handler}]
   ["/healthcheck" {:get handler}]
   ["/:channel" {:get channel-handler}]
   ["/:channel/:date" {:get handler}]])
