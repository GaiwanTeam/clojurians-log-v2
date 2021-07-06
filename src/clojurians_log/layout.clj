(ns clojurians-log.layout
  (:require [lambdaisland.ornament :as o]
            [clojure.java.io :as io]))

(defn base [body]
  [:html
   [:head
    [:title "Clojurians log v2"]
    [:meta {:charset "UTF-8"}]
    [:meta {:content "width=device-width, initial-scale=1" :name "viewport"}]
    (when (io/resource "public/css/compiled/style.css")
      [:link {:rel "stylesheet" :href "/css/compiled/style.css"}])
    (if (io/resource "public/css/compiled/ornament.css")
      [:link {:rel "stylesheet" :href "/css/compiled/ornament.css"}]
      [:style {:type "text/css" :id "ornament"} (o/defined-styles)])]
   [:body
    [:div#app
     body]
    #_[:script {:type "application/javascript" :src (str "/ui/" (get-script-name :main))}]]])