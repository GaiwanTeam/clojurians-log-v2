(ns clojurians-log.http
  (:require [ring.adapter.jetty :as jetty]
            [lambdaisland.ornament.watcher :as o-watcher]
            [lambdaisland.ornament :as o]
            [lambdaisland.hiccup :as hiccup]
            [clojure.java.io :as io]
            [muuntaja.core :as m]
            [muuntaja.format.core :as muuntaja-format]
            [reitit.ring.middleware.muuntaja :as muuntaja-middleware]
            [integrant.core :as ig]
            [reitit.ring :as ring])
  (:import (java.io OutputStream)
           (org.eclipse.jetty.server Server)))

(o/defstyled block :div
  :border
  :bg-red-100
  :w-16
  :h-4)

(defn layout [body]
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

(defn html-encoder
  "Muuntaja encoder that renders HTML
  Expects a Clojure collection with a `:view-fn` in the metadata, which takes
  the `:body` collection as argument, and returns the body as a string."
  [opts]
  (reify muuntaja-format/EncodeToBytes
    (encode-to-bytes [_ data charset]
      (let [view (get (meta data) :view-fn)
            rendered (view data)]
        (.getBytes ^String rendered ^String charset)))
    muuntaja-format/EncodeToOutputStream
    (encode-to-output-stream [_ data charset]
      (fn [^OutputStream output-stream]
        (let [view (get (meta data) :view-fn)
              rendered (view data)]
          (.write output-stream (.getBytes ^String rendered ^String charset)))))))

(defn muuntaja-instance
  "Create a Muuntaja instance that includes HTML handling
  Can take options just like [[muuntaja.core/create]],
  see [[muuntaja.core/default-options]]."
  ([]
   (muuntaja-instance m/default-options))
  ([opts]
   (m/create
    (-> opts
        (assoc :default-format "text/html")
        (assoc-in [:formats "text/html"]
                  (muuntaja-format/map->Format
                   {:name :html
                    :encoder [html-encoder]}))))))

(defn test-handler [request]
  {:status 200
   :body "Hello test!!!"})

(defn handler [request]
  {:status 200
   :view (comp layout
               (fn [data]
                 [block [:a {:href "#"} "Lol"]]))
   :body {:e 123}})

(defn routes []
  [["/" {:get handler}]
   ["/ww/" {:get test-handler}]])

(defn view-fn-middleware [handler]
  (fn [request]
    (let [resp (handler request)]
      (if-let [view (get resp :view)]
        (update resp :body vary-meta assoc :view-fn
                (comp hiccup/render view))))))

(defn app []
  (ring/ring-handler
   (ring/router
    (routes)
    {:data {:muuntaja   (muuntaja-instance)
            :middleware [muuntaja-middleware/format-middleware
                         view-fn-middleware
                         ]}})
   (ring/routes
    (ring/redirect-trailing-slash-handler {:method :add})
    (ring/create-resource-handler {:path "/" :root "public"})
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "Page not found."})}))))

(defmethod ig/init-key ::server [_ config]
  (jetty/run-jetty #((app) %)
                   (-> config (assoc :join? false))))

(defmethod ig/halt-key! ::server [_ server]
  (.stop server))

(defmethod ig/init-key ::css [_ config]
  (o-watcher/start-watcher! config))

(defmethod ig/halt-key! ::css [_ hawk]
  (o-watcher/stop-watcher! hawk))
