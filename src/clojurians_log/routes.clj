(ns clojurians-log.routes
  (:require [clojurians-log.layout :as layout]
            [lambdaisland.ornament :as o]))

(o/defstyled block :div
  :border
  :bg-red-100
  :w-16
  :h-4)

(defn test-handler [request]
  {:status 200
   :body "Hello test!!!"})

(defn handler [request]
  {:status 200
   :view (comp layout/base
               (fn [data]
                 [block [:a {:href "#"} "Lol"]]))
   :body {:e 123}})

(defn routes []
  [["/" {:get handler}]
   ["/ww/" {:get test-handler}]])
