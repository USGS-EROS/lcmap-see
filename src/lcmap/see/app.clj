(ns lcmap.see.app
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [clojusc.twig :as logger]
            [lcmap.see.components :as components]
            [lcmap.see.util :as util])
  (:gen-class))

(defn -main
  "This is the entry point. Note, however, that the system components are
  defined in lcmap.see.components. In particular, lcmap.see.components.system
  brings together all the defined (and active) components; that is the module
  which is used to bring the system up when (component/start ...) is called.

  'lein run' will use this as well as 'java -jar'."
  [& args]
  ;; Set the initial log-level before the components set the log-levels for
  ;; the configured namespaces
  (logger/set-level! ['lcmap] :info)
  (let [system (components/init)
        local-ip  (.getHostAddress (java.net.InetAddress/getLocalHost))]
    (log/info "LCMAP SEE service's local IP address:" local-ip)
    (component/start system)
    (util/add-shutdown-handler #(component/stop system))))
