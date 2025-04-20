(ns clojurians-log.db.protocols)

(defprotocol Storage
  (all-channels [this] "List all channels"))
