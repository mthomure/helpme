(ns helpme.io
  (:require [clojure.data.csv :as csv]
            [clojure.string :as s]))

(defn serialize [fname x]
  (log/info "serialize" fname)
  (spit fname (pr-str x)))

(defn deserialize [fname]
  (log/info "deserialize" fname)
  (read-string (slurp fname)))

(defn get-clipboard []
  (let [contents (-> (java.awt.Toolkit/getDefaultToolkit)
                     .getSystemClipboard
                     (.getContents nil))
        flavor java.awt.datatransfer.DataFlavor/stringFlavor]
    (when (and contents (.isDataFlavorSupported contents flavor))
      (.getTransferData contents flavor))))

(def cb-owner (reify java.awt.datatransfer.ClipboardOwner
             (lostOwnership [this clipboard contents] ())))

(defn set-clipboard [text]
  (let [cb (.getSystemClipboard (java.awt.Toolkit/getDefaultToolkit))
        text (java.awt.datatransfer.StringSelection. text)]
    (.setContents cb text cb-owner)))

(defn rel->tsv
  ([rel] (rel->tsv (-> rel first keys) rel))
  ([ks rel]
   (let [ks (map #(s/replace (name %) "-" "_") ks)]
     (println (s/join "\t" ks)))
   (doseq [m rel]
     (println (s/join "\t" (map #(get m %) ks))))))

(defn rel->clipboard
  ([rel] (rel->clipboard (-> rel first keys) rel))
  ([ks rel] (set-clipboard (with-out-str (rel->tsv ks rel)))))

(defn rel->csv
  "write a relation to disk as CSV"
  ([fname rel]
   (rel->csv fname (-> rel first keys) rel))
  ([fname ks rel]
   (with-open [w (clojure.java.io/writer fname)]
     (let [ks (map #(s/replace (name %) "-" "_") ks)]
       (csv/write-csv w [ks]))
     (csv/write-csv w (->> rel (map (apply juxt ks)))))))
