(ns clojurians-log.config
  (:require [lambdaisland.config :as config]))

(def !config (atom nil))

(defn get [k]
  (config/get @!config k))

(defn init! []
  (reset! !config (config/create {:env :dev :prefix "app"})))
