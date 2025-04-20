(ns user)

(defmacro jit [sym]
  `(requiring-resolve '~sym))

(defn go [& [opts]]
  ((jit clojurians-log.system/stop!) #_ opts)
  ((jit clojurians-log.system/go) opts))

(defn browse []
  ((jit clojure.java.browse/browse-url)
   (str "http://localhost:"
        ((jit clojurians-log.config/get) :http/port))))
