(ns user)

(defmacro jit [sym]
  `(requiring-resolve '~sym))

(defn go [& [opts]]
  ((jit clojurians-log.system/go) opts))

(defn halt [& [opts]]
  ((jit integrant.repl/halt)))

(defn system []
  @(jit integrant.repl.state/system))

(defn migrations-config []
  (-> (system) :clojurians-log.db.core/migrations))

(defn ds []
  (-> (system) :clojurians-log.db.core/datasource))

(defn migrate []
  (let [mc (migrations-config)]
    ((jit clojurians-log.db.migrations/migrate) mc)))

(defn browse []
  ((jit clojure.java.browse/browse-url)
   "http://localhost:8000"))
