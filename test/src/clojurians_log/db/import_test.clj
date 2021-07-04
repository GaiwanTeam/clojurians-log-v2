(ns src.clojurians-log.db.import-test
  (:require [clojure.test :refer :all]
            [src.clojurians-log.db.import :as sut]))

(def message->channel-join
  {:type "message",
   :subtype "channel_join",
   :ts "1519960433.000040",
   :user "U9GN58S2C",
   :text "<@U9GN58S2C> has joined the channel"})

(def message
  {:type "message",
   :text "what are you doing?",
   :user "U9HC42SGJ",
   :ts "1520090384.000036",
   :team "T9GR4MC0Z",
   :user_team "T9GR4MC0Z",
   :source_team "T9GR4MC0Z",})

(def message->thread
  {:source_team "T9GR4MC0Z",
   :type "message",
   :thread_ts "1596984225.001900",
   :ts "1597042101.000200",
   :parent_user_id "UR7AB6KLN",
   :team "T9GR4MC0Z",
   :client_msg_id "9a37cf1d-adcc-4fba-9632-17fa72e5643b",
   :blocks
   [{:type "rich_text",
     :block_id "op1",
     :elements
     [{:type "rich_text_section",
       :elements
       [{:type "text",
         :text
         "I'm leaning towards the Lenovo suggestion \n\nMom's asking me to go with HP since Lenovo laptops have a bad rep in my family "}
        {:type "emoji", :name "stuck_out_tongue"}]}]}],
   :user_team "T9GR4MC0Z",
   :user "UR7AB6KLN",
   :reactions [{:name "pfftch", :users ["U9GN58S2C"], :count 1}],
   :text
   "I'm leaning towards the Lenovo suggestion \n\nMom's asking me to go with HP since Lenovo laptops have a bad rep in my family :stuck_out_tongue:"}
  )

(def message->bot-message
  {:type "message",
   :subtype "bot_message",
   :text "Test",
   :ts "1620900365.000100",
   :username "Eye Of Sauron",
   :bot_id "B012FF4413L"})

(def message->tombstone
  {:subscribed true,
   :reply_users ["U9GN58S2C" "UM4RQHQ0J"],
   :type "message",
   :thread_ts "1581152693.002400",
   :reply_users_count 2,
   :ts "1581152693.002400",
   :replies
   [{:user "U9GN58S2C", :ts "1581152735.002800"}
    {:user "UM4RQHQ0J",
     :ts "1581152750.003400"}],
   :hidden true,
   :last_read "1581152750.003400",
   :is_locked false,
   :user "USLACKBOT",
   :reply_count 2,
   :latest_reply "1581152750.003400",
   :subtype "tombstone",
   :text "This message was deleted."})

(def message->channel-purpose
  {:type "message",
   :subtype "channel_purpose",
   :ts "1582886076.000300",
   :user "UM4RQHQ0J",
   :text "<@UM4RQHQ0J> set the channel description: Blog posts",
   :purpose "Blog posts"})

(deftest event-tx
  {})
