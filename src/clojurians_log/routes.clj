(ns clojurians-log.routes
  (:require [clojurians-log.layout :as layout]
            [clojurians-log.components.home :as home]
            [clojurians-log.db.queries :as queries]
            [lambdaisland.ornament :as o]))

(defn handler [{:keys [ds] :as request}]
  (let [msgs (queries/all-messages ds)]
    {:status 200
     :view (fn [data]
             [layout/base
              [home/section data]])
     :body {:channels (queries/all-channels ds)
            :messages msgs}}))

(defn routes []
  [["/" {:get handler}]
   ["/about" {:get handler}]
   ["/sitemap" {:get handler}]
   ["/healthcheck" {:get handler}]
   ["/:channel" {:get handler}]
   ["/:channel/:date" {:get handler}]])
