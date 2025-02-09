(ns clojurians-log.db.archive
  (:require
   [clojurians-log.db.protocols :as proto]))

(defrecord ArchiveStorage []
  proto/Storage
  (all-channels [this]))
