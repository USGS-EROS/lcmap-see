(ns ^{:doc
  "This is the job runner for the CCDC model that is run in a docker container.
  Ideally, this runner will be used by an instance of the LCMAP REST server
  that is *not* running in Docker, but instead being run (deployed) on a
  non-virtualized operating system."}
  lcmap.see.model.ccdc-docker
  (:require [clojure.tools.logging :as log]
            [clj-commons-exec :as exec]
            [lcmap.see.job.tracker :as jt]
            [lcmap.see.util :as util])
  (:import [java.io ByteArrayOutputStream]))

(def dockerhub-org "usgseros")
(def dockerhub-repo "debian-c-ccdc")
(def docker-tag (format "%s/%s" dockerhub-org dockerhub-repo))

(defn exec-docker-run
  ""
  [[job-id row col in-dir out-dir scene-list verbose]]
  (let [verbose-flag (util/make-flag "--verbose" verbose :unary? true)
        in-dir-flag (util/make-flag "--inDir" in-dir)
        out-dir-flag (util/make-flag "--outDir" out-dir)
        row-flag (util/make-flag "--row" row)
        col-flag (util/make-flag "--col" col)
        scene-list-flag (util/make-flag "--sceneList" scene-list)
        cmd (remove nil? ["/usr/bin/sudo"
                          "/usr/bin/docker"
                          "run" "-t" docker-tag
                          row-flag col-flag in-dir-flag out-dir-flag
                          scene-list-flag verbose-flag])
        result @(exec/sh cmd)]
    (case (:exit result)
      0 (:out result)
      1 (:err result)
      [:error "unexpected output" result])))

(defn run-model [component job-id default-row result-table
                 row col in-dir out-dir scene-list verbose]
  ;; Define some vars for pedagogical clarity
  (let [func #'exec-docker-run
        args [job-id row col in-dir out-dir scene-list verbose]]
    (log/debugf "run-model has [func args]: [%s %s]" func args)
    (jt/track-job component
                  job-id
                  default-row
                  result-table
                  [func args])))
