(ns user)

(defmacro jit [sym]
  `(requiring-resolve '~sym))

(defn go [& [opts]]
  ((jit clojurians-log.system/go) opts))

(defn system []
  @(jit integrant.repl.state/system))

(defn migrations-config []
  (-> (system) :clojurians-log.db.core/migrations))
