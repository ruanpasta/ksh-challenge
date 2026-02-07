(ns api.db
  (:require [api.config :as config]
            [next.jdbc :as jdbc])
  (:import [com.zaxxer.hikari HikariDataSource]))

(def ds
  (doto (HikariDataSource/new)
    (.setJdbcUrl (:url config/default))
    (.setUsername (:user config/default))
    (.setPassword (:password config/default))))

(comment
  ;; test ds (datasource)
  (jdbc/execute! ds ["select 1"])
  
  nil)
