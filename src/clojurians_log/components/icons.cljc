(ns clojurians-log.components.icons)

;; make sure to give icons a width and height css styling

(def menu
  [:svg
   {:fill "none" :stroke "currentColor" :viewbox "0 0 24 24" :xmlns "http://www.w3.org/2000/svg"}
   [:path {:stroke-linecap "round"
           :stroke-linejoin "round"
           :stroke-width "2"
           :d "M4 6h16M4 12h16M4 18h16"}]])
