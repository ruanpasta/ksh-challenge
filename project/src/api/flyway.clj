(ns api.flyway
  (:require [api.config :as config])
  (:import (org.flywaydb.core Flyway)))

(defn migrate
  [{:keys [url user password locations schema]
    :or   {url (:url config/default)
           user (:user config/default)
           password (:password config/default)
           locations (:locations config/default)}}]
  (-> (Flyway/configure)
      (.dataSource url user password)
      (.schemas (into-array String [schema]))
      (.locations (into-array String locations))
      (.load)
      (.migrate)))

(defn clean
  [{:keys [url user password locations schema]
    :or   {url (:url config/default)
           user (:user config/default)
           password (:password config/default)
           locations (:locations config/default)}}]
  (println "cleaning DB")
  (-> (Flyway/configure)
      (.dataSource (:url config/default)
                   (:user config/default)
                   (:password config/default))
      (.schemas (into-array String ["api"]))
      (.locations (into-array String (:locations config/default)))
      (.cleanDisabled false)
      (.load)
      (.clean)))

(comment
  (migrate)
  (clean)
  
  nil)
