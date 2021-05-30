(ns org.oxal.clojurians-log.http
  (:require [ring.adapter.jetty :as jetty]
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

(def server
  (jetty/run-jetty #((app) %) {:port 8080 :join? false}))

(comment
  (.stop server))
