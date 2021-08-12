(ns clojurians-log.components.home)

(defn sidebar []
  [:div
   {:class
    "bg-indigo-900 text-purple-300 flex-none p-4 hidden md:block"}
   [:div {:class "cursor-pointer mb-4"}
    [:div
     {:class
      "bg-white h-12 w-12 flex items-center justify-center text-black text-2xl font-semibold rounded-lg mb-1 overflow-hidden"}
     [:img {:alt "", :src "https://twitter.com/tailwindcss/profile_image"}]]
    [:div {:class "text-center text-white opacity-50 text-sm"} "⌘1"]]
   [:div {:class "cursor-pointer mb-4"}
    [:div
     {:class
      "bg-indigo-300 opacity-25 h-12 w-12 flex items-center justify-center text-black text-2xl font-semibold rounded-lg mb-1 overflow-hidden"}
     "L"]
    [:div {:class "text-center text-white opacity-50 text-sm"} "⌘2"]]
   [:div {:class "cursor-pointer"}
    [:div
     {:class
      "bg-white opacity-25 h-12 w-12 flex items-center justify-center text-black text-2xl font-semibold rounded-lg mb-1 overflow-hidden"}
     [:svg
      {:class "fill-current h-10 w-10 block",
       :viewbox "0 0 20 20",
       :xmlns "http://www.w3.org/2000/svg"}
      [:path
       {:d
        "M16 10c0 .553-.048 1-.601 1H11v4.399c0 .552-.447.601-1 .601-.553 0-1-.049-1-.601V11H4.601C4.049 11 4 10.553 4 10c0-.553.049-1 .601-1H9V4.601C9 4.048 9.447 4 10 4c.553 0 1 .048 1 .601V9h4.399c.553 0 .601.447.601 1z"}]]]]]
  )

(defn channel-list [channels]
  [:div
   {:class
    "bg-indigo-700 text-purple-300 flex-none w-64 pb-6 hidden md:block overflow-hidden overflow-y-scroll"}
   [:div {:class "text-white mb-2 mt-3 px-4 flex justify-between"}
    [:div {:class "flex-auto"}
     [:h1 {:class "font-semibold text-xl leading-tight mb-1 truncate"}
      "Clojurians Log"]
     [:div {:class "flex items-center mb-6"}
      [:svg
       {:class "h-2 w-2 fill-current text-green mr-2", :viewbox "0 0 20 20"}
       [:circle {:cx "10", :cy "10", :r "10"}]]
      [:span {:class "text-white opacity-50 text-sm"} "Clojurians Log"]]]
    [:div
     [:svg
      {:class "h-6 w-6 fill-current text-white opacity-25",
       :viewbox "0 0 20 20"}
      [:path
       {:d
        "M14 8a4 4 0 1 0-8 0v7h8V8zM8.027 2.332A6.003 6.003 0 0 0 4 8v6l-3 2v1h18v-1l-3-2V8a6.003 6.003 0 0 0-4.027-5.668 2 2 0 1 0-3.945 0zM12 18a2 2 0 1 1-4 0h4z",
        :fill-rule "evenodd"}]]]]
   [:div {:class "mb-8"}
    [:div {:class "px-4 mb-2 text-white flex justify-between items-center"}
     [:div {:class "opacity-75"} "Channels"]]
    (for [channel channels]
      [:div {:class "bg-teal-dark py-1 px-4 text-white"} (str "# " (:name channel))])]
   [:div
    [:div {:class "px-4 mb-2 text-white flex justify-between items-center"}
     [:div {:class "opacity-75"} "Apps"]]]])

(defn top-bar []
  [:div {:class "border-b flex px-6 py-2 items-center flex-none"}
   [:div {:class "flex flex-col"}
    [:h3 {:class "text-grey-900 mb-1 font-extrabold"} "#general"]
    [:div {:class "text-grey-dark text-sm truncate"}
     "Chit-chattin' about ugly HTML and mixing of concerns."]]
   [:div {:class "ml-auto hidden md:block"}
    [:div {:class "relative"}
     [:input
      {:class "appearance-none border border-grey rounded-lg pl-8 pr-4 py-2",
       :placeholder "Search",
       :type "search"}]
     [:div
      {:class "absolute inset-y-0 left-0 pl-3 flex items-center justify-center"}
      [:svg
       {:class "fill-current text-grey h-4 w-4",
        :viewbox "0 0 20 20",
        :xmlns "http://www.w3.org/2000/svg"}
       [:path
        {:d
         "M12.9 14.32a8 8 0 1 1 1.41-1.41l5.35 5.33-1.42 1.42-5.33-5.34zM8 14A6 6 0 1 0 8 2a6 6 0 0 0 0 12z"}]]]]]])

(defn message [{:keys [image-192 text display-name]}]
  [:div {:class "flex items-start mb-4 text-sm"}
   [:img
    {:class "w-10 h-10 rounded mr-3",
     :src image-192}]
   [:div {:class "flex-1 overflow-hidden"}
    [:div [:span {:class "font-bold"} display-name]
     [:span {:class "text-grey text-xs"} "11:46"]]
    [:p {:class "text-black leading-normal"}
     text]]])

(defn message-with-code []
  [:div {:class "flex items-start mb-4 text-sm"}
   [:img
    {:class "w-10 h-10 rounded mr-3",
     :src ""}]
   [:div {:class "flex-1 overflow-hidden"}
    [:div [:span {:class "font-bold"} "Clojurians log"]
     [:span {:class "text-grey text-xs"} "12:45"]]
    [:p {:class "text-black leading-normal"}
     "How are we supposed to control the marquee space without an utility for it? I propose this:"]
    [:div
     {:class
      "bg-grey-300 border border-grey-light text-grey-900 text-sm font-mono rounded p-3 mt-2 whitespace-pre overflow-scroll"}
     ".marquee-lightspeed { -webkit-marquee-speed: fast; } .marquee-lightspeeder { -webkit-marquee-speed: faster; }"]]])


(defn message-with-tag []
  [:div {:class "flex items-start mb-4 text-sm"}
   [:img
    {:class "w-10 h-10 rounded mr-3",
     :src "https://twitter.com/davidhemphill/profile_image"}]
   [:div {:class "flex-1 overflow-hidden"}
    [:div [:span {:class "font-bold"} "David Hemphill"]
     [:span {:class "text-grey text-xs"} "12:46"]]
    [:p {:class "text-black leading-normal"}
     [:a
      {:class "inline-block bg-blue-100 text-blue no-underline",
       :href "#"} "@Clojurians log"]
     " the size of the generated CSS is creating a singularity in space/time, we must stop adding more utilities before it's too late!"]]])

(defn section [{:keys [messages channels]}]
  [:div {:class "font-sans antialiased h-screen flex"}
   (channel-list channels)
   #_[:comment " Chat content "]
   [:div {:class "flex-1 flex flex-col bg-white overflow-hidden"}
    (top-bar)
    #_[:comment " Chat messages "]
    [:div {:class "px-6 py-4 flex-1 overflow-y-scroll"}
     (for [msg messages]
       (message msg))]
    #_[:div {:class "pb-6 px-4 flex-none"}
       [:div {:class "flex rounded-lg border-2 border-grey overflow-hidden"}
        [:span {:class "text-3xl text-grey border-r-2 border-grey p-2"}
         [:svg
          {:class "fill-current h-6 w-6 block w-64",
           :viewbox "0 0 20 20",
           :xmlns "http://www.w3.org/2000/svg"}
          [:path
           {:d
            "M16 10c0 .553-.048 1-.601 1H11v4.399c0 .552-.447.601-1 .601-.553 0-1-.049-1-.601V11H4.601C4.049 11 4 10.553 4 10c0-.553.049-1 .601-1H9V4.601C9 4.048 9.447 4 10 4c.553 0 1 .048 1 .601V9h4.399c.553 0 .601.447.601 1z"}]]]
        [:input
         {:class "w-full px-4",
          :placeholder "Message #general",
          :type "text"}]]]]])
