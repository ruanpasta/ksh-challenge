(ns api.routes
  (:require [api.controller :as ctrl]))

(def routes
  #{["/activities"
     :get
     `[ctrl/activities]
     :route-name
     :list-activities]
    
    ["/activities/import"
     :post
     `[ctrl/import-activities]
     :route-name
     :import-activities]})
