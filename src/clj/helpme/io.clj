(ns helpme.io
  (:import java.io.PushbackReader)
  (:require [clojure.data.csv :as csv]
            [clojure.edn :as edn]
            [cheshire.core :as json]
            [cognitect.transit :as transit]
            [clojure.string :as s]
            [clojure.java.io :refer
             [reader writer input-stream output-stream]]))

;;;; CLOJURE RELATIONS TO CSV

;; http://blog.jayfields.com/2011/01/clojure-select-keys-select-values-and.html
(defn select-values [m ks]
  (reduce #(conj %1 (m %2)) [] ks))

(defn rel->xsv
  "Write relation to *out* as delimited ascii.

  rel - (coll of maps) relation to write
  sep - (str) value separator
  cols - (coll) subset of keys to write

  if missing, keys are inferred from first map in relation.
  "
  [dest rel & {:keys [sep cols]}]
  (let [sep (or sep \,)
        cols (or cols (keys (first rel)))
        headers (map name cols)]
    (with-open [w (writer dest)]
      (csv/write-csv w [headers] :separator sep)
      (csv/write-csv w (map #(select-values % cols) rel) :separator sep))))

(defn xsv->rel
  "Read delimited ascii from *in* as a relation."
  [source & {:keys [sep]}]
  (let [sep (or sep \,)
        [ks & rows] (with-open [r (reader source)]
                      (doall (clojure.data.csv/read-csv r :separator sep)))
        ks (mapv keyword ks)]
    (mapv #(zipmap ks %) rows)))

(defn csv->rel [source]
  (xsv->rel source :sep \,))

(defn rel->csv [dest rel]
  (rel->xsv dest rel :sep \,))

(defn tsv->rel [source]
  (xsv->rel source :sep \tab))

(defn rel->tsv [dest rel]
  (rel->xsv dest rel :sep \tab))

;;;; SERIALIZATION

(defn write-clojure [source obj]
  (with-open [w (writer source)]
    (binding [*out* w]
      (pr obj))))

(defn read-clojure [source]
  (with-open [r (PushbackReader. (reader source))]
    (edn/read r)))

(defn write-json [source obj]
  (with-open [w (writer source)]
    (json/generate-stream obj w)))

(defn read-json [source]
  (with-open [r (reader source)]
    (json/parse-stream r keyword)))

(def TRANSIT-PROTOCOL :json)

(defn write-transit [source obj & [protocol]]
  (let [protocol (or protocol TRANSIT-PROTOCOL)
        writer (transit/writer (output-stream source) protocol)]
    (transit/write writer obj)))

(defn read-transit [source & [protocol]]
  (let [protocol (or protocol TRANSIT-PROTOCOL)
        reader (transit/reader (input-stream source) protocol)]
    (transit/read reader)))

;;;; CLIPBOARD I/O

(def cb-owner (reify java.awt.datatransfer.ClipboardOwner
                (lostOwnership [this clipboard contents] ())))

(defn ->clipboard [text]
  (let [cb (.getSystemClipboard (java.awt.Toolkit/getDefaultToolkit))
        text (java.awt.datatransfer.StringSelection. text)]
    (.setContents cb text cb-owner)))

(defn clipboard-> []
  (let [contents (-> (java.awt.Toolkit/getDefaultToolkit)
                     .getSystemClipboard
                     (.getContents nil))
        flavor java.awt.datatransfer.DataFlavor/stringFlavor]
    (when (and contents (.isDataFlavorSupported contents flavor))
      (.getTransferData contents flavor))))

(defmacro with-clipboard
  "Evaluates body in a context in which *out* is bound to the clipboard."
  [& body]
  `(->clipboard (with-out-str ~@body)))

;;;; USAGE EXAMPLES

(comment
  ;; Copy relation to clipboard as TSV, for pasting into a spreadsheet.
  (with-clipboard (rel->tsv *out* rel)))
