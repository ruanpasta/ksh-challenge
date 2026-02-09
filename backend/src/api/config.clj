(ns api.config)

(def default {:url       "jdbc:postgresql://localhost:5432/api_db"
              :user      "api_user"
              :password  "api_pass"
              :locations ["classpath:db/migration"]})
