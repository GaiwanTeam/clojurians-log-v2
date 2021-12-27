(ns user)

(defmacro jit [sym]
  `(requiring-resolve '~sym))

(defn go [& [opts]]
  ((jit clojurians-log.system/go) opts))

(defn system []
  @(jit integrant.repl.state/system))

