(ns slack
  (:require
   [clojure.java.io :as io]
   [clojurians-log.db.import :as import]
   [clojurians-log.db.queries :as queries]
   [clojurians-log.db.bulk-import :as bulk-import]
   [clojurians-log.slack.api :as clj-slack]
   [clojurians-log.system :as system]
   [clojurians-log.utils :as utils]
   [honey.sql :as sql]
   [integrant.repl.state :as ig-state]
   [next.jdbc :as jdbc])
  )

(def ds (user/ds))

(def slack-conn (clj-slack/conn (get-in (system/secrets) [:slack-socket :bot-token])))

(queries/all-channels ds)
(def imported-channels (bulk-import/channel-import-from-api! slack-conn))

imported-channels

(def imported-users (bulk-import/member-import-from-api! slack-conn))

imported-users

(queries/all-messages ds)
