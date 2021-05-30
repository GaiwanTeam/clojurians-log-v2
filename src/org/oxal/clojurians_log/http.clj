(ns org.oxal.clojurians-log.http
  (:require [ring.adapter.jetty :as jetty]
            [integrant.core :as ig]
            [reitit.ring :as ring]))

(defn test-handler [request]
  {:status 200
   :body "Hello test!!!"})

(defn handler [request]
  {:status 200
   :body "Hello world"})

(defn routes []
  [["/" {:get handler}]
   ["/ww/" {:get test-handler}]])

(defn app []
  (ring/ring-handler
   (ring/router (routes))
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
