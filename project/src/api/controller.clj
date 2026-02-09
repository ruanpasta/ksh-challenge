(ns api.controller
  (:require [api.db-activities :as db-activities]
            [clj-http.client :as client]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [io.pedestal.interceptor :as intr]
            [malli.core :as m]
            [malli.error :as me]
            [api.schemas :as schemas]))

(defn file->data
  [file planned?]
  (mapv
   (fn [[created-at activity activity-type unit amount]]
     [(hash [created-at activity activity-type unit])
      (java.sql.Date/valueOf created-at)
      activity
      activity-type
      unit
      (if planned? (bigdec amount) 0)
      (if-not planned? (bigdec amount) 0)])
   (rest file)))

(def import-activities
  (intr/interceptor
   {:name ::import-activities
    :enter
    (fn [ctx]
      (let [{:keys [ds request]} ctx
            tempfile             (get-in request [:multipart-params "file" :tempfile])
            file                 (->> tempfile (io/reader) (csv/read-csv))
            planned?             (str/includes? (first file) "planned")
            data                 (file->data file planned?)
            valid-data?          (m/validate schemas/ActivityCSV data)
            response             (when (not-empty data)
                                   (if planned?
                                     (db-activities/import-csv ds {:type "planned" :values data})
                                     (db-activities/import-csv ds {:type "executed" :values data})))]
        (cond
          (not valid-data?) (assoc ctx :response {:status 400 :body {:error "Invalid CSV!"
                                                                     :info  (me/humanize (m/explain schemas/ActivityCSV data))}})
          (nil? response)   (assoc ctx :response {:status 500 :body "Empty file!"})
          response          (assoc ctx :response {:status 200 :body "OK" })
          :else             (assoc ctx :response {:status 500 :body (str request)}))))}))

(def activities
  (intr/interceptor
   {:name ::get-activities
    :enter
    (fn [ctx]
      (let [{:keys [ds request]} ctx
            query-params         (:query-params request)
            response             (db-activities/get-activities
                                  ds
                                  {:date          (when (:date query-params)                                                    
                                                    (->> query-params :date (java.sql.Date/valueOf)))
                                   :activity      (:activity query-params)
                                   :activity-type (:activity-type query-params)})]
        (if response
          (assoc ctx :response {:status 200 :body response})
          (assoc ctx :response {:status 500 :body (str request)}))))}))

(comment
  (client/post
   "http://localhost:8080/activities/import"
   {:multipart [{:name         "file"
                 :content      (io/file "../challenge/ruan-pasta-2026-02-05_executed.csv")
                 :content-type "text/csv"}]})

  (client/post
   "http://localhost:8080/activities/import"
   {:multipart [{:name         "file"
                 :content      (io/file "../challenge/ruan-pasta-2026-02-05_planned.csv")
                 :content-type "text/csv"}]})

  (client/get
   "http://localhost:8080/activities?date=2025-07-18&activity-type=Building&activity=Walling")
  
  nil)
