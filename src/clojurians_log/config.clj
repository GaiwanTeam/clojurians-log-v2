(ns clojurians-log.config
  (:require [lambdaisland.config :as config]))

(def config
  (config/create
   {:env :dev :prefix "clojurians-log"}))

(defn get [k]
  (config/get config k))

(get :slack-socket/bot-token)
(config/reload! config)
