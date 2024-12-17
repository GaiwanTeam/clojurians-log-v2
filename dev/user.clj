(ns user)

(defmacro jit [sym]
  `(requiring-resolve '~sym))

(defn go [& [opts]]
  ((jit clojurians-log.system/stop!) opts)
  ((jit clojurians-log.system/go) opts))

(defn browse []
  ((jit clojure.java.browse/browse-url)
   "http://localhost:8000"))
