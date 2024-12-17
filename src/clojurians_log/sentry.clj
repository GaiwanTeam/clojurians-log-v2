(ns clojurians-log.sentry
  (:require [clojurians-log.config :as config])
  (:import (io.sentry Sentry)))

(defn init! []
  (when-let [sentry-dsn (config/get :sentry/dsn)]
    (.init Sentry (fn [opts]
                    (doto opts
                      (.setDsn sentry-dsn)
                      (.setTracesSampleRate 1.0)
                      (.setDebug true))))))
