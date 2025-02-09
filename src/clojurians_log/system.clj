(ns clojurians-log.system
  (:require
   [clojurians-log.config]
   [clojurians-log.sentry]
   [clojurians-log.db]
   [clojurians-log.http]
   [clojure.java.io :as io]
   [clojure.edn :as edn]))

(defn stop! []
  (clojurians-log.http/stop-server))

(defn go [& [{:keys [profile]
              :or {profile :dev}}]]
  (println "Starting with profile: " profile)
  (clojurians-log.sentry/init!)
  (clojurians-log.db/init!)
  (clojurians-log.http/start-server)
  #_(clojurians-log.slack.socket/init!))
