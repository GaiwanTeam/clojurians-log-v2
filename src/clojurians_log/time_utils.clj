(ns clojurians-log.time-utils
  (:require [clojure.string :as str])
  (:import [java.time Instant LocalDate]
           [java.time.format DateTimeFormatter]))

(defn ts->inst
  "Convert a Slack timestamp like \"1433399521.000490\" into a java.time.Instant like
  #inst \"2015-06-04T06:32:01.000490Z\""
  [ts]
  (let [[seconds micros] (map #(Double/parseDouble %)
                              (str/split ts #"\."))
        inst (Instant/ofEpochSecond seconds (* 1e3 micros))]
    inst))
