(ns katamorph.edn.sorted
  "Invertible EDN export for admitted values.

   clio.shape.canonical/canonical-form is the hash preimage. It is not a
   printer for the value: reading canonical-edn yields the semantic vector,
   not the original map. export-edn orders maps and sets by pr-str so the
   text is a function of the value, then import-edn reads that text with the
   host EDN reader. Tests also read it with clio.shape.edn/read-one and check
   that canonical-form is unchanged. A clio value-level import, once one
   exists on main, should replace import-edn."
  (:require #?(:clj [clojure.edn :as edn]
               :cljs [cljs.reader :as edn])))

(defn- print-unlimited
  "pr-str ignores a caller's *print-length* or *print-level* binding.

   A bound length of 1 would print both `[1 2]` and `[1 3]` as `[1 ...]`,
   and a sorted set would then drop one of them."
  [value]
  (binding [*print-length* nil
            *print-level* nil
            *print-dup* false
            *print-namespace-maps* false
            *print-readably* true]
    (pr-str value)))

(defn order-value
  "Sort maps and sets recursively. Vectors keep their order."
  [value]
  (cond
    (map? value)
    (into (sorted-map-by #(compare (print-unlimited %1) (print-unlimited %2)))
          (map (fn [[k v]] [k (order-value v)]) value))

    (set? value)
    (into (sorted-set-by #(compare (print-unlimited %1) (print-unlimited %2)))
          (map order-value value))

    (vector? value)
    (mapv order-value value)

    :else value))

(defn export-edn
  "Deterministic EDN text. Key order in the input does not change the text."
  [value]
  (print-unlimited (order-value value)))

(defn import-edn
  "Read one EDN value produced by export-edn."
  [text]
  (edn/read-string text))

(defn round-trip
  [value]
  (import-edn (export-edn value)))
