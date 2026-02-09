(ns api.interceptors)

(defn inject-ds-interceptor [ds]
  {:name ::inject-ds
   :enter (fn [ctx]
            (assoc ctx :ds ds))})

(def error-interceptor
  {:name  ::error-interceptor
   :error (fn [ctx ex]
            (println "ERRO NO SERVIDOR:" (ex-message ex))

            (assoc ctx :response
                   {:status (-> ex ex-data :status)
                    :body   {:error   "Internal server error"
                             :details (ex-message ex)}}))})
