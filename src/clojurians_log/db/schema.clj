(ns clojurians-log.db.schema
  (:require
   [malli.core :as m]
   [next.jdbc :as jdbc]))

(def message
  [:map
   [:id :int]
   [:channel_id :int]
   [:member_id :int]
   [:text :string]
   [:ts :string]
   [:parent {:optional true} :int]
   [:deleted_ts :string]])

