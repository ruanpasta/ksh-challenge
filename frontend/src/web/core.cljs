(ns web.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.dom.client :as rdomc]
            [web.service :as service]
            [reagent.core :as r]
            [web.state :refer [app-state]]
            [web.components :as c]))

(defn app
  []
  (r/with-let [_ (service/get-activities)]
    [:div.container
     [:h1 "Activities"]
     [:div.form
      [c/date-input]
      [c/select-activity]
      [c/select-activity-type]]
     [c/file-import-button {:on-change
                            #(go (<! (service/handle-import-csv %))
                                 (service/get-activities))}]
     (if (not-empty (:activities @app-state))
       [c/table {:rows (:activities @app-state)}]
       [:p "No data found!"])]))

(defonce react-root (rdomc/create-root (.getElementById js/document "app")))

(defn run
  []
  (rdomc/render react-root [app]))
