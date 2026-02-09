(ns web.dev.server
  (:require
   [clojure.java.shell :refer [sh]]
   [shadow.cljs.devtools.api :as shadow]
   [shadow.cljs.devtools.server :as server]))

(defn cljs-repl
  "Switches to shadow-cljs-repl with :build-id"
  ([build-id]
   (shadow/nrepl-select build-id)))

(defn setup-code! []
  (sh "npm" "install")
  (prn (str "using-node:" (:out (sh "node" "--version")))))

(defn open-browser! []
  (sh "open" "http://localhost:8085"))

(defn start-server!
  "Starts server and watchers for builds"
  []
  (let [main :web]
    (server/start!)
    (shadow/watch main)
    (cljs-repl main)))

(defn stop-server!
  "Stops server"
  []
  (server/stop!))

(comment
  (start-server!)
  (stop-server!)
  (cljs-repl :web-app)
  (cljs-repl :browser-test))
