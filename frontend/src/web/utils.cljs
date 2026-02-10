(ns web.utils)

(defn format-date
  [date]
  (when date
    (.format
     (js/Intl.DateTimeFormat.
      "en-CA")
     date)))
