(ns clojurians-log.db
  (:require
   [clojurians-log.config :as config]
   [migratus.core :as migratus]
   [next.jdbc :as jdbc]
   [next.jdbc.date-time :as jdbc.date-time]))

(def ds (atom nil))

(defn get-migration-config []
  {:store                :database
   :migration-dir        "migrations/"
   ;; :init-script          "init.sql"
   ;; :init-in-transaction? false
   :migration-table-name "migrations"
   :db {:datasource (jdbc/get-datasource @ds)}})

(defn execute! [& args]
  (apply jdbc/execute! @ds args))

(defn init []
  (migratus/init (get-migration-config)))

(defn migrate-create [name]
  (migratus/create (get-migration-config) name))

(defn migrate []
  (migratus/migrate (get-migration-config)))

(defn migrate-rollback []
  "rollback the migration with the latest timestamp"
  (migratus/rollback (get-migration-config)))

(defn migrate-up [id]
  "bring up migrations matching the ids"
  (migratus/up (get-migration-config) 20111206154000))

(defn migrate-down [id]
  "bring down migrations matching the ids"
  (migratus/down (get-migration-config) 20111206154000))

(defn init! []
  ;; TODO: add connection pooling
  (let [data-source (jdbc/get-datasource {:dbtype (config/get :db/type)
                                          :user (config/get :db/user)
                                          :port (config/get :db/port)
                                          :password (config/get :db/password)
                                          :dbname (config/get :db/name)
                                          :serverTimezone "UTC"})]
    (jdbc.date-time/read-as-instant)
    (reset! ds (jdbc/with-options data-source jdbc/unqualified-snake-kebab-opts))
    (migrate)))

(comment
  (migrate)
  )
