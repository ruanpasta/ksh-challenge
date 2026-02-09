(ns integration.core
  (:require [api.flyway :as flyway]
            [io.pedestal.connector :as conn]
            [io.pedestal.http.jetty :as jet])
  (:import [com.zaxxer.hikari HikariDataSource]
           [org.testcontainers.containers PostgreSQLContainer]))

(defn get-route
  [routes kw]
  (first (for [r     routes
               :let  [y (nth r 4)]
               :when (= y kw)]
           {:url    (nth r 0)
            :method (nth r 1)})))

(def ^:dynamic *db-container* nil)

(def ^:dynamic *connector* nil)

(defn with-postgres
  [image-name]
  (fn [tests]
    (let [container (PostgreSQLContainer/new image-name)]
      (.start container)
      (binding [*db-container* container]
        (tests))
      (.stop container))))

(defn- make-datasource []
  (doto (HikariDataSource/new)
    (.setJdbcUrl (.getJdbcUrl *db-container*))
    (.setUsername (.getUsername *db-container*))
    (.setPassword (.getPassword *db-container*))))

(defn with-ds
  [system]
  (fn [tests]
    (let [ds (make-datasource)]
      (swap! system assoc :ds ds)
      (tests)
      (.close ds))))

(defn with-connector
  [service system]
  (fn [tests]
    (binding [*connector* (-> (service 8888 (:ds @system))
                              (jet/create-connector nil))]
      (tests))))

(defn with-server
  [system]
  (fn [tests]
    (swap! system assoc :server                                  
           (conn/start! *connector*))
    (tests)
    (conn/stop! (:server @system))))
