(ns clojurians-log.layout
  (:require [lambdaisland.ornament :as o]
            [clojure.java.io :as io]))

(defn base
  ([body]
   (base nil body))
  ([extra-head body]
   [:html
    [:head
     [:meta {:charset "UTF-8"}]
     [:meta {:content "width=device-width, initial-scale=1" :name "viewport"}]
     [:script {:data-goatcounter "https://clojurians-log-v2.goatcounter.com/count"
               :async true
               :src "//gc.zgo.at/count.js"}]
     (when (io/resource "public/css/compiled/style.css")
       [:link {:rel "stylesheet" :href "/assets/css/compiled/style.css"}])
     (if (io/resource "public/css/compiled/ornament.css")
       [:link {:rel "stylesheet" :href "/assets/css/compiled/ornament.css"}]
       [:style {:type "text/css" :id "ornament"} (o/defined-styles)])
     extra-head]
    [:body
     [:div#app
      body]
     [:script {:type "application/javascript" :src (str "/assets/js/main.js")}]]]))
