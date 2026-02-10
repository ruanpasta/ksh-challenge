(ns web.service
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [web.state :refer [app-state]]
            [web.utils :as u]))

(def ^:private base-url "http://localhost:8080")

(defn get-activities
  []
  (go
    (let [
          params (->> (select-keys @app-state [:date :activity :activity-type])
                      (remove #(empty? (second %)))
                      (map (fn [[k v]] [(name k) v]))
                      (into {}))
          query  (str "?" (js/URLSearchParams. (clj->js params)))
          url    (str base-url "/activities" query)
          resp   (<! (http/get url))]
      (swap! app-state assoc :activities (:body resp)))))

(defn handle-import-csv
  [file]
  (go
    (<! (http/post (str base-url "/activities/import")
                                     {:multipart-params [["file" file]]}))))
