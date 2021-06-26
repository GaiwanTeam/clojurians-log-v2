(ns clojurians-log.utils)

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
