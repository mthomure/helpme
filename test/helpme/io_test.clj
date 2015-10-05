(ns helpme.io-test
  (:require [midje.sweet :refer :all]
            [helpme.io :refer :all]))

(defmacro loopback [expr-out expr-in]
  `(with-in-str (with-out-str ~expr-out) ~expr-in))

(fact
 "read/write relation as CSV"
 (let [rel [{:a "1" :b "2" :c "3"} {:a "10" :b "20" :c "30"}]]
   (loopback (rel->csv *out* rel) (csv->rel *in*) => rel)))

(fact
 "read/write relation as TSV"
 (let [rel [{:a "1" :b "2" :c "3"} {:a "10" :b "20" :c "30"}]]
   (loopback (rel->tsv *out* rel) (tsv->rel *in*)) => rel))

(fact
 "read/write as clojure"
 (let [obj {:a 1 :b 2.1 :c ["a" "b"] :d {:d1 "d1v"}}]
   (loopback (write-clojure *out* obj) (read-clojure *in*)) => obj))

(fact
 "read/write as JSON"
 (let [obj {:a 1 :b 2.1 :c ["a" "b"] :d {:d1 "d1v"}}]
   (loopback (write-json *out* obj) (read-json *in*)) => obj))

(defn loopback-transit [obj proto]
  (let [baos (java.io.ByteArrayOutputStream.)]
    (write-transit baos obj proto)
    (read-transit (java.io.ByteArrayInputStream. (.toByteArray baos)) proto)))

(fact
 "read/write as transit (JSON)"
 (let [obj {:a 1 :b 2.1 :c ["a" "b"] :d {:d1 "d1v"}}]
   (loopback-transit obj :json) => obj))

(fact
 "read/write as transit (MSGPACK)"
 (let [obj {:a 1 :b 2.1 :c ["a" "b"] :d {:d1 "d1v"}}]
   (loopback-transit obj :msgpack) => obj))

(fact
 "read/write clipboard"
 (let [msg "fancy dancy clipboard usage"]
   (do (->clipboard msg) (clipboard->)) => msg
   (do (with-clipboard (print msg)) (clipboard->)) => msg))
