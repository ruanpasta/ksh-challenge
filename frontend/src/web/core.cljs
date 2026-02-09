(ns web.core
  (:require [reagent.dom.client :as rdomc]))

(defn app
  []
  [:div.container "Hello World!"])

(defonce react-root (rdomc/create-root (.getElementById js/document "app")))

(defn run
  []
  (rdomc/render react-root [app]))
