(ns clojurians-log.http
  (:require [ring.adapter.jetty :as jetty]
            [clojurians-log.config :as config]
            [lambdaisland.ornament :as o]
            [lambdaisland.hiccup :as hiccup]
            [clojurians-log.routes :as routes]
            [clojure.java.io :as io]
            [muuntaja.core :as m]
            [muuntaja.format.core :as muuntaja-format]
            [reitit.ring.middleware.muuntaja :as muuntaja-middleware]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring :as ring])
  (:import (java.io OutputStream)
           (org.eclipse.jetty.server Server)))

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

(defn view-fn-middleware [handler]
  (fn [request]
    (let [resp (handler request)]
      (if-let [view (get resp :view)]
        (update resp :body vary-meta assoc :view-fn
                (comp hiccup/render view))))))

(defn app []
  (ring/ring-handler
   (ring/router
    (routes/routes)
    {:conflicts nil
     :data {:muuntaja   (muuntaja-instance)
            :middleware [muuntaja-middleware/format-middleware
                         parameters/parameters-middleware
                         view-fn-middleware]}})
   (ring/routes
    (ring/redirect-trailing-slash-handler {:method :add})
    (ring/create-resource-handler {:path "/assets" :root "public"})
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "Page not found."})}))))

(def http-server (atom nil))

(defn start-server []
  (println (str "Starting jetty on http://localhost:" (config/get :http/port)))
  (let [config {}
        server (jetty/run-jetty #((app) %) {:port (config/get :http/port)
                                            :join? false})]
    (reset! http-server server)))

(defn stop-server []
  (when-let [server @http-server]
    (.stop server)))
