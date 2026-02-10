(ns api.config)

(def default {:url       (or (System/getenv "DB_URL")
                             "jdbc:postgresql://localhost:5432/api_db")
              :user      "api_user"
              :password  "api_pass"
              :locations ["classpath:db/migration"]})
