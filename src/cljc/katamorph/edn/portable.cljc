(ns katamorph.edn.portable
  "Portable EDN admitted into access shapes.

   The numeric half reuses the schema kernel's portable-number law and adds
   the two bounds clio's canonical form also enforces: finiteness, and the
   Gregorian instant range canonical-form will hash. Admission refuses a value
   here so export does not have to truncate it later."
  (:require [clojure.string :as str]
            [katamorph.schema.condition :as condition]
            #?(:clj [clojure.edn :as edn]
               :cljs [cljs.reader :as edn])))

(def min-instant-ms
  "1582-10-15, the earliest instant clio.shape.canonical will hash."
  -12219292800000)

(def max-instant-ms
  "Last millisecond of year 9999, clio's latest readable EDN instant."
  253402300799999)

(defn finite-portable-number?
  [value]
  (and (condition/portable-number? value)
       (< ##-Inf value ##Inf)))

(defn portable-instant?
  "A standard #inst whose millisecond value sits in clio's Gregorian range."
  [value]
  (and (inst? value)
       (let [millis (inst-ms value)
             text (pr-str value)]
         (and (number? millis)
              (<= min-instant-ms millis max-instant-ms)
              (str/starts-with? text "#inst \"")))))

(defn portable-keyword?
  "A keyword whose printed form reads back as the same keyword.

   `(keyword \"foo bar\")` is a keyword on the JVM, and its printer does not
   emit readable EDN. Map keys get the same check."
  [value]
  (and (keyword? value)
       (let [text (binding [*print-length* nil
                           *print-level* nil]
                    (pr-str value))]
         (try
           (let [reread (edn/read-string text)]
             (and (keyword? reread)
                  (= (namespace value) (namespace reread))
                  (= (name value) (name reread))))
           (catch #?(:clj Exception :cljs :default) _ false)))))

(defn portable-edn?
  "True when value is data both runtimes can print and clio can canonicalize."
  [value]
  (cond
    (or (nil? value) (boolean? value) (string? value)) true
    (portable-keyword? value) true
    (uuid? value) true
    (portable-instant? value) true
    (finite-portable-number? value) true
    (vector? value) (every? portable-edn? value)
    (set? value) (every? portable-edn? value)
    (map? value) (every? (fn [[k v]]
                           (and (or (portable-keyword? k) (string? k))
                                (portable-edn? v)))
                         value)
    :else false))

(def PortableEdn
  [:fn {:error/message
        "value must be portable EDN: finite safe numbers, #inst, and no functions"}
   portable-edn?])
