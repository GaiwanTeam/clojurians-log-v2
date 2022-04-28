(ns clojurians-log.slack.socket
  "Slack Socket App which maintains a websocket connection to Slack
  and receives real time events."
  (:require [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as cske]
            [clojure.edn :as edn]
            [clojure.java.data :as jd]
            [integrant.core :as ig])
  (:import (com.slack.api.bolt App AppConfig)
           (com.slack.api.bolt.handler BoltEventHandler)
           (com.slack.api.bolt.socket_mode SocketModeApp)
           (com.slack.api.model.event
            ChannelCreatedEvent ChannelRenameEvent
            MessageEvent MessageChangedEvent MessageDeletedEvent MessageRepliedEvent
            MessageChannelJoinEvent
            ReactionAddedEvent ReactionRemovedEvent)))

(set! *warn-on-reflection* true)

(defmulti handle-event (fn [ds event] (:type event)))

(defmethod handle-event :default [ds event]
  (println "Default event called")
  (let [event (cske/transform-keys csk/->kebab-case-keyword event)]
    (prn event))
  (println "-------"))

(defn create-handler
  "Create a handler with dispatch-evt"
  [ds]
  (let [handle-event* (partial handle-event ds)]
    (reify BoltEventHandler
      (apply [_ payload ctx]
        (let [event (jd/from-java-deep (.getEvent payload) {})
              #_internal-event #_(evt-format/socket->internal m)]
          #_(tap> m)
          (handle-event* event))
        (.ack ctx)))))

(defn create-app-conf [slack-bot-token]
  (.build
   (doto (AppConfig/builder)
     (.singleTeamBotToken slack-bot-token))))

(defn create-socket-app [{:keys [ds slack-bot-token slack-app-token]}]
  (let [app-conf (create-app-conf slack-bot-token)
        handler (create-handler ds)
        app (doto (App. app-conf)
              (.event MessageEvent handler)
              (.event MessageChangedEvent handler)
              (.event MessageRepliedEvent handler)
              (.event MessageDeletedEvent handler)
              (.event MessageChannelJoinEvent handler)
              (.event ChannelRenameEvent handler)
              (.event ChannelCreatedEvent handler)
              (.event ReactionAddedEvent handler)
              (.event ReactionRemovedEvent handler))]
    (doto (SocketModeApp. ^String slack-app-token app)
      (.startAsync))))

(defmethod ig/init-key ::app [_ config]
  ;; (log/info :socket-app/starting :now)
  (create-socket-app config))

(defmethod ig/halt-key! ::app [_ ^SocketModeApp socket-app]
  ;; (log/info :socket-app/stoping :now)
  (.stop ^SocketModeApp socket-app))

(comment

  ,)
