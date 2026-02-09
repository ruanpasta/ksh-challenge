(ns api.schemas)

(def ActivityCSVRow
  [:tuple
   int?
   inst?
   string?
   [:enum "Building" "Road" "Railway"]
   [:enum "m" "m2" "m3" "L"]
   number?
   number?])

(def ActivityCSV
  [:vector ActivityCSVRow])
