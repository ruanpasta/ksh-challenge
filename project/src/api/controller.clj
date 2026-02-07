(ns api.controller
  (:require [clj-http.client :as client]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [io.pedestal.interceptor :as intr]
            [clojure.string :as str]
            [api.db-activities :as db-activities]))

(defn- file->data
  [file planned?]
  (mapv
   (fn [[date activity activity-type unit amount]]
     [(hash [date activity activity-type unit])
      (java.sql.Date/valueOf date)
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
            response             (when (not-empty data)
                                   (if planned?
                                     (db-activities/import-csv ds {:type "planned" :values data})
                                     (db-activities/import-csv ds {:type "executed" :values data})))]
        (if response
          (assoc ctx :response {:status 200 :body "OK" })
          (assoc ctx :response {:status 500 :body (str request)}))))}))

(def activities
  (intr/interceptor
   {:name ::get-activities
    :enter
    (fn [ctx]
      (let [{:keys [ds request]} ctx
            query-params         (:query-params request)
            response             (db-activities/get-activities
                                  ds
                                  {:date          (->> query-params :date (java.sql.Date/valueOf))
                                   :activity      (:activity query-params)
                                   :activity-type (:activity-type query-params)})]
        (if response
          (assoc ctx :response {:status 200 :body response})
          (assoc ctx :response {:status 500 :body (str request)}))))}))

(comment
  (client/post
   "http://localhost:8080/activities/import"
   {:multipart [{:name         "file"
                 :content      (clojure.java.io/file "../ruan-pasta-2026-02-05_executed.csv")
                 :content-type "text/csv"}]})

  (client/post
   "http://localhost:8080/activities/import"
   {:multipart [{:name         "file"
                 :content      (clojure.java.io/file "../ruan-pasta-2026-02-05_planned.csv")
                 :content-type "text/csv"}]})

  (client/get
   "http://localhost:8080/activities?date=2025-07-19&activity-type=Building&activity=Walling")
  

  nil)
