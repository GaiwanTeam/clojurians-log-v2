(ns repl-sessions.slack-archive
  (:require
   [clojurians-log.config :as config]
   [co.gaiwan.slack.archive :as archive]
   [co.gaiwan.slack.normalize :as normalize]
   [co.gaiwan.slack.raw-archive :as raw]))

(def cljians-log-dir "/home/arne/repos/clojurians-log")

(def raw-events (raw/dir-event-seq cljians-log-dir))

(time
 (def arch (archive/raw->archive cljians-log-dir (archive/archive "/tmp/cljians-archive"))))

;;=> "Elapsed time: 33486.306643 msecs"



(def archive (archive/fetch-api-resources arch (config/get :slack-socket/bot-token)))

(enrich/enrich
 (normalize/message-tree
  (archive/slurp-chan-day-raw arch "C010HTVBU0N" "2020-08-10")))
