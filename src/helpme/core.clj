(ns helpme.core
  (:require [clojure.reflect :as reflect]
            [clojure.pprint :as pprint]))

;; TODO: make these dynamic
;; number of string chars to include in summary
(def MAX-STR-LEN 50)
;; number of seq entries (or map keys) to include in summary
(def MAX-SEQ-LEN 30)
(def SEQ-ELLIPSIS (symbol "..."))
(def STR-ELLIPSIS "...")

(defn summary* [x]
  (letfn [(type-class [x]
            (cond (or (string? x) (number? x)) :scalar
                  (sequential? x) :seq
                  (map? x) :map
                  :else :default))
          (short-str [s]
            (cond
              (string? s) (if (> (count s) MAX-STR-LEN)
                            (let [size (- MAX-STR-LEN (count STR-ELLIPSIS))]
                              (str (subs s 0 size) STR-ELLIPSIS))
                            s)
              (keyword? s) (-> s name short-str keyword)
              (symbol? s) (-> s name short-str symbol)))
          (short-seq [xs]
            (let [ys (map short-str xs)]
              (if (> (count ys) MAX-SEQ-LEN)
                (let [head (take (dec MAX-SEQ-LEN) ys)]
                  (conj (vec head) SEQ-ELLIPSIS))
                ys)))]
    (when x
      (try
        (let [tc (type-class x)
              r (case tc
                  :scalar (if (string? x)
                            {:size (count x)
                             :value (short-str x)}
                            {:value x})
                  :seq {:size (count x)}
                  :map {:size (count x)
                        :value {:keys (short-seq (sort (keys x)))}}
                  :default nil)]
          (merge r {:type (type x) :type-class tc}))
        (catch Exception e
          (throw (RuntimeException.
                  "Error computing summary of data structure" e)))))))

(defmacro summary [x]
  `(let [x# ~x]
     (-> x# summary* (assoc :form '~x))))

(defn print-summary [summary]
  (if summary
    (let [{:keys [form type size value]} summary]
      (when form (print (str form " ")))
      (print (.getSimpleName type))
      (when size (print (str "[" size "]")))
      (when value
        (println)
        (pprint/pprint value)))
    (println "nil")))

;; NOTE: forces evaluation of x.
(defmacro show [x]
  `(print-summary (summary ~x)))

(defn object-methods
  "find sorted list of method names on an object"
  [x]
  (->> (reflect/reflect x)
       :members
       (filter :exception-types)
       (map :name)
       set
       sort))
