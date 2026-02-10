(ns web.state
  (:require [reagent.core :as r]))

(def app-state (r/atom {:activity "" :activity-type ""}))
