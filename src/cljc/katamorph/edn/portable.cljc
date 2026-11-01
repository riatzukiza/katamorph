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

(defn- integral-number?
  [value]
  (or (integer? value)
      #?(:clj (and (instance? Double value)
                   (== value (Math/rint (double value))))
         :cljs false)))

(defn finite-portable-number?
  "Finite, and an integral value must sit in the safe-integer range on both hosts."
  [value]
  (and (condition/portable-number? value)
       (< ##-Inf value ##Inf)
       (or (not (integral-number? value))
           (condition/portable-integer? value))))

(defn- preserved-instant?
  "java.util.Date and a CLJS date. A Timestamp prints nanoseconds the #inst reader drops."
  [value]
  #?(:clj (and (instance? java.util.Date value)
               (not (instance? java.sql.Timestamp value)))
     :cljs (inst? value)))

(defn portable-instant?
  "A standard #inst whose millisecond value sits in clio's Gregorian range
   and reads back as the same millisecond."
  [value]
  (and (preserved-instant? value)
       (let [millis (inst-ms value)
             text (binding [*print-length* nil
                            *print-level* nil
                            *print-readably* true]
                    (pr-str value))]
         (and (number? millis)
              (<= min-instant-ms millis max-instant-ms)
              (str/starts-with? text "#inst \"")
              (try (= millis (inst-ms (edn/read-string text)))
                   (catch #?(:clj Exception :cljs :default) _ false))))))

(defn portable-keyword?
  "A keyword both EDN readers accept.

   The JVM reader accepts `:foo//bar` (a name that starts with `/`). The
   ClojureScript reader does not. The read-back also excludes a keyword whose
   printer is not EDN, such as `(keyword \"foo bar\")`."
  [value]
  (and (keyword? value)
       (not (str/blank? (name value)))
       (not (str/starts-with? (name value) "/"))
       (let [text (binding [*print-length* nil
                            *print-level* nil
                            *print-readably* true]
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
    (set? value) (and (every? portable-edn? value)
                      (let [numbers (filter number? value)]
                        (not (some (fn [n]
                                     (some #(and (not (identical? n %))
                                                 (== n %)
                                                 (not= n %))
                                           numbers))
                                   numbers))))
    (map? value) (every? (fn [[k v]]
                           (and (or (portable-keyword? k) (string? k))
                                (portable-edn? v)))
                         value)
    :else false))

(def PortableEdn
  [:fn {:error/message
        "value must be portable EDN: finite safe numbers, #inst, and no functions"}
   portable-edn?])
