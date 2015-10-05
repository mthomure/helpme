(ns helpme.logging
  (:import [org.apache.log4j Logger Level]))

(def LEVELS
  {:fatal Level/FATAL
   :warn  Level/WARN
   :info  Level/INFO
   :debug Level/DEBUG
   :off   Level/OFF})

;; Examples:
;; - see messages about flambo serialization
;;   (set-log-level! :debug "flambo")
;; - get gobs of debugging information (i.e., using the root logger)
;;   (set-log-level! :debug)
(defn set-log-level! [level & classes]
  (let [level (LEVELS level)
        loggers (if classes
                  (map #(Logger/getLogger %) classes)
                  [(Logger/getRootLogger)])]
    (for [logger loggers]
      (.setLevel logger level))))

(defmacro with-log-level [cls level & body]
  `(let [with-lev#  (LEVELS ~level)
         logger#    (if ~cls (Logger/getLogger ~cls) (Logger/getRootLogger))
         prev-lev#  (.getLevel logger#)]
     (try
       (.setLevel logger# with-lev#)
       ~@body
       (finally
         (.setLevel logger# prev-lev#)))))
