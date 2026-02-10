(ns web.components
  (:require [reagent.core :as r]
            [web.service :as s]
            [web.state :refer [app-state]]
            [web.utils :as u]))

(defn select-activity
  []
  (fn []
    [:div.input
     [:label {:for "activity"} "Activity"]
     [:select {:id        "activity"
               :value     (:activity @app-state)
               :on-change #(do
                             (swap! app-state assoc :activity (-> % .-target .-value))
                             (s/get-activities))}
      [:option {:value ""} ""]
      [:option {:value "Pavement"} "Pavement"]
      [:option {:value "Subbase application"} "Subbase application"]
      [:option {:value "Walling"} "Walling"]
      [:option {:value "Coat preparation"} "Coat preparation"]
      [:option {:value "Ballast"} "Ballast"]
      [:option {:value "Foundation pouring"} "Foundation pouring"]
      [:option {:value "Terrain leveling"} "Terrain leveling"]
      [:option {:value "Iron mining"} "Iron mining"]
      [:option {:value "Removing trees"} "Removing trees"]
      [:option {:value "Anchorage"} "Anchorage"]
      [:option {:value "Cement making"} "Cement making"]
      [:option {:value "Laying steel rail"} "Laying steel rail"]
      [:option {:value "Rolling/Tractor"} "Rolling/Tractor"]]]))

(defn select-activity-type
  []
  (fn []
    [:div.input
     [:label {:for "activity-type"} "Activity Type"]
     [:select {:id        "activity-type"
               :value     (:activity-type @app-state)
               :on-change #(do
                             (swap! app-state assoc :activity-type (-> % .-target .-value))
                             (s/get-activities))}
      [:option {:value ""} ""]
      [:option {:value "Railway"} "Railway"]
      [:option {:value "Road"} "Road"]
      [:option {:value "Building"} "Building"]]]))

(defn date-input
  []
  (fn []
    [:div.input
     [:label {:for "my-date"} "Date"]
     [:input {:type      "date"
              :id        "created_at"
              :value     (:date @app-state)
              :on-change #(do
                            (swap! app-state assoc :date (-> % .-target .-value))
                            (s/get-activities))}]]))

(defn table
  [{:keys [rows]}]  
  [:table.pretty-table
   [:thead
    [:tr
     [:th "ID"]
     [:th "Date"]
     [:th "Activity"]
     [:th "Type"]
     [:th "Unit"]
     [:th "Planned"]
     [:th "Executed"]]]
   [:tbody
    (when rows
      (map
       (fn [{:keys [id created_at activity activity_type unit planned_amount executed_amount]}]
         [:tr {:key id}
          [:td id]
          [:td (u/format-date created_at)]
          [:td activity]
          [:td activity_type]
          [:td unit]
          [:td {:style {:text-align "right"}} (when planned_amount (.toFixed planned_amount 2))]
          [:td {:style {:text-align "right"}} (when executed_amount (.toFixed executed_amount 2))]])
       rows))]])

(defn file-import-button [{:keys [on-change]}]
  (let [input-ref (r/atom nil)]
    (fn []
      [:div
       [:input {:style {:display "none"}
                :ref #(reset! input-ref %)
                :type "file"
                :on-change #(do
                              (when on-change
                                (on-change (-> % .-target .-files (aget 0))))
                              (set! (-> % .-target .-value) ""))}]
       [:button.import-button {:on-click #(.click @input-ref)}
        "Import CSV"]])))
