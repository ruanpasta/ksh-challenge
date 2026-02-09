(ns integration.activities-test
  (:require [api.core :refer [service]]
            [api.flyway :as flyway]
            [clj-http.client :as client]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.test :refer :all]
            [integration.core :as tc]
            [io.pedestal.connector.test :refer :all]
            [next.jdbc :as jdbc]
            [next.jdbc.result-set :as rs]
            [api.controller :as sut]
            [io.pedestal.http.route :as route]
            [api.routes :refer [routes]]))

(def system (atom {}))

(def ^:private base-url "http://localhost:8888/")

(def ^:private account
  {:username "testname"
   :password "test_password"
   :email    "test@email.com"})

(defn with-migrations
  [tests]
  (flyway/migrate {:url      (.getJdbcUrl tc/*db-container*)
                   :user     (.getUsername tc/*db-container*)
                   :password (.getPassword tc/*db-container*)
                   :schema   "api"})
  (tests))

(use-fixtures :once
  (tc/with-postgres "postgres:18")
  (tc/with-ds system)
  (tc/with-connector service system)
  (tc/with-server system)
  with-migrations)

(deftest upload-csv-success
  []
  (testing "should import a planned csv successfully"
    (let [file     (io/file "./test/resources/planned-activities.csv")
          response (client/post
                    (str base-url "activities/import")
                    {:multipart [{:name         "file"
                                  :content      file
                                  :content-type "text/csv"}]
                     :throw-exceptions false})]
      (is (= 200 (:status response)))
      (is (= "OK" (:body response)))))

  (testing "should import a executed csv successfully"
    (let [file     (io/file "./test/resources/executed-activities.csv")
          response (client/post
                    (str base-url "activities/import")
                    {:multipart [{:name         "file"
                                  :content      file
                                  :content-type "text/csv"}]
                     :throw-exceptions false})]
      (is (= 200 (:status response)))
      (is (= "OK" (:body response)))))

  (testing "should have twenty activities with no duplicates"
    (let [activities-count (:count (jdbc/execute-one!
                                    (:ds @system)
                                    ["SELECT COUNT(*) FROM api.activities;"]
                                    {:return-keys true
                                     :builder-fn  rs/as-unqualified-lower-maps}))]
      (is (= 20 activities-count)))))

(deftest updload-csv-error
  []
  (testing "should not import a invalid csv"
    (let [file     (io/file "./test/resources/invalid-activities.csv")
          response (client/post
                    (str base-url "activities/import")
                    {:multipart        [{:name         "file"
                                         :content      file
                                         :content-type "text/csv"}]
                     :accept           :edn
                     :as               :edn
                     :throw-exceptions false})]
      (is (= 400 (:status response)))
      (is (= "Invalid CSV!" (->> response :body (edn/read-string) :error)))))

  (testing "should return file not found"
    (let [file     "invalid file"
          response (client/post
                    (str base-url "activities/import")
                    {:multipart        [{:name         "file"
                                         :content      file
                                         :content-type "text/csv"}]
                     :accept           :edn
                     :as               :edn
                     :throw-exceptions false})]
      (is (= 404 (:status response)))
      (is (= "Not Found" (:body response))))))

(deftest list-activities
  []
  (jdbc/execute-one! (:ds @system) ["DELETE FROM api.activities;"])
  (testing "should list the imported activities"
    (let [file     (io/file "./test/resources/planned-activities.csv")
          _        (client/post
                    (str base-url "activities/import")
                    {:multipart        [{:name         "file"
                                         :content      file
                                         :content-type "text/csv"}]
                     :throw-exceptions false})
          response (response-for tc/*connector* :get "/activities")]
      (is (= 200 (:status response)))
      (is (= 10 (-> response :body (edn/read-string) (count))))))

  (testing "should filter the imported activities by date"
    (let [response (response-for tc/*connector* :get "/activities?date=2025-01-04")]
      (is (= 200 (:status response)))
      (is (= 1 (-> response :body (edn/read-string) (count))))
      (is (= "Ballast" (-> response :body (edn/read-string) (first) :activity)))))

  (testing "should filter the imported activities by activity"
    (let [response (response-for tc/*connector* :get "/activities?activity=Ballast")]
      (is (= 200 (:status response)))
      (is (= 1 (-> response :body (edn/read-string) (count))))
      (is (= "Ballast" (-> response :body (edn/read-string) (first) :activity)))))

  (testing "should filter the imported activities by activity-type"
    (let [response (response-for tc/*connector* :get "/activities?activity-type=Railway")]
      (is (= 200 (:status response)))
      (is (= 4 (-> response :body (edn/read-string) (count))))
      (is (= "Railway" (-> response :body (edn/read-string) (first) :activity_type)))))

  (testing "should filter the imported activities by date, activity and activity-type"
    (let [response (response-for tc/*connector* :get "/activities?date=2025-01-04&activity=Ballast&activity-type=Railway")]
      (is (= 200 (:status response)))
      (is (= 1 (-> response :body (edn/read-string) (count))))
      (is (= "Ballast" (-> response :body (edn/read-string) (first) :activity)))
      (is (= "Railway" (-> response :body (edn/read-string) (first) :activity_type))))))
