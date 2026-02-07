(ns api.db-activities
  (:require [next.jdbc :as jdbc]
            [clojure.java.io :as io]
            [next.jdbc.result-set :as rs]))

(defn- log
  [e]
  (println "JDBC ERROR: " (ex-message e))
  (.printStackTrace e)
  (throw e))

(def ^:private queries
  {:import-planned  (slurp (io/resource "sql/activities/import-planned.sql"))
   :import-executed (slurp (io/resource "sql/activities/import-executed.sql"))
   :list            (slurp (io/resource "sql/activities/list.sql"))})

(defn import-csv
  [ds {:keys [type values]}]
  (let [sql (if (= type "planned")
              (:import-planned queries)
              (:import-executed queries))]
    (try
      (jdbc/with-transaction [tx ds]
        (jdbc/execute-batch!
         tx
         sql
         values
         {:batch-size 500}))
      (catch Exception e
        (log e)))))

(defn get-activities
  [ds {:keys [date activity activity-type]}]
  (try
    (jdbc/execute!
     ds
     [(:list queries) date date
      activity activity
      activity-type activity-type]
     {:return-keys true
      :builder-fn  rs/as-unqualified-lower-maps})
    (catch Exception e
      (log e))))
