(ns api.core
  (:require [api.db :refer [ds]]
            [api.flyway :as flyway]
            [api.interceptors :as intc]
            [api.routes :refer [routes]]
            [io.pedestal.connector :as conn]
            [io.pedestal.http.jetty :as jet]
            [io.pedestal.http.ring-middlewares :as ring-middlewares]
            [io.pedestal.http.cors :as cors]
            [io.pedestal.http :as http]))

(def server (atom nil))

(defn service
  [port ds]
  (-> (conn/default-connector-map port)
      (conn/with-interceptor
        (cors/allow-origin
         {:allowed-origins (constantly true)
          :creds true}))
      (conn/with-default-interceptors)
      (conn/with-interceptor (intc/inject-ds-interceptor ds))
      (conn/with-interceptor (ring-middlewares/multipart-params))
      (conn/with-interceptor intc/error-interceptor)
      (conn/with-routes routes)))

(defn start
  []
  (flyway/migrate {:schema "api"})
  (reset! server
          (-> (service 8080 ds)
              (jet/create-connector nil)
              (conn/start!))))

(defn stop
  []
  (when @server (conn/stop! @server)))

(defn -main
  []
  (start))

(comment
  (start)
  (stop)

  nil)
