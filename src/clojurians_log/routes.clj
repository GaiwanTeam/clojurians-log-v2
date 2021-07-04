(ns clojurians-log.routes
  (:require [clojurians-log.layout :as layout]
            [clojurians-log.components.home :as home]
            [clojurians-log.db.queries :as queries]
            [lambdaisland.ornament :as o]))

(o/defstyled block :div
  :border
  :bg-red-100
  :w-16
  :h-4)

(defn test-handler [request]
  {:status 200
   :body "Hello test!!!"})

(defn handler [{:keys [ds] :as request}]
  (let [msgs (queries/all-messages ds)]
    {:status 200
     :view (comp layout/base
                 (fn [data]
                   [:div
                    #_[block [:a {:href "#"} "Lol"]]
                    [home/section msgs]
                    ]))
     :body {:e 123}}))

(defn routes []
  [["/" {:get handler}]
   ["/ww/" {:get test-handler}]])
