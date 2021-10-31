(ns clojurians-log.utils
  (:require [clojure.data.json :as json]
            [camel-snake-kebab.core :as csk]
            [clojure.java.io :as io]))

(defn select-keys-nested-as
  [m paths]
  (let [select (fn select [p]
                 (cond
                   (map? p)
                   [(:rename p) (second (select (:keys p)))]
                   (coll? p)
                   [(last p) (get-in m p)]
                   :else
                   [p (get m p)]))]
    (into {}
          (map select)
          paths)))

(defn read-json-from-file [file]
  (-> file
      slurp
      (json/read-str :key-fn csk/->kebab-case-keyword)))
