(ns helpme.format
  (:require [clojure.pprint :as pprint]))

(defn truncate-str
  "Take (at most) length length characters from head of string."
  ([s length]
   (truncate-str s length nil))
  ([s length suffix]
   (let [c (count s)]
     (if (> c length)
       (str (subs s 0 (- length 3)) suffix)
       s))))

(defn print-table
  ([xs] (print-table (keys (first xs)) xs))
  ([ks xs]
   (letfn [(format-value [v]
             (cond
               (integer? v) (format "%,d" v)
               (float? v) (if (< (Math/abs v) 1)
                            (format "%.4f" v)
                            (format "%,.2f" v))
               (string? v) (u/truncate-str v 20 "...")
               :else v))
           (format-map [m]
             (zipmap (keys m) (map format-value (vals m))))]
     (pprint/print-table ks (map format-map xs)))))

(defn safe+ [x y]
  (when (and (number? x) (number? y)) (+ x y)))

(defn print-totals [xs]
  (print-table [(apply merge-with safe+ xs)]))
