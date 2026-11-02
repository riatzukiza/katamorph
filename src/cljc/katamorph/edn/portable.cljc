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

(def ^:private keyword-part
  "EDN keyword characters. Colon is allowed after the first character.
   No-break space is not: the ClojureScript reader treats it as whitespace
   and the JVM reader does not."
  #"[A-Za-z*+!_'?<>=][A-Za-z0-9*+!_'?$%&=<>.#:-]*")

(defn portable-keyword?
  "A keyword both EDN readers accept.

   The JVM reader accepts `:foo//bar` and a name containing U+00A0. The
   ClojureScript reader does not. The part pattern excludes both, and the
   read-back excludes a keyword whose printer is not EDN."
  [value]
  (and (keyword? value)
       (boolean (re-matches keyword-part (name value)))
       (or (nil? (namespace value))
           (boolean (re-matches keyword-part (namespace value))))
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

(defn- as-cljs-number
  "Numbers as ClojureScript compares them: every number is a double."
  [value]
  (cond
    (number? value) (double value)
    (map? value) (into {} (map (fn [[k v]] [(as-cljs-number k) (as-cljs-number v)])) value)
    (set? value) (into #{} (map as-cljs-number) value)
    (vector? value) (mapv as-cljs-number value)
    (sequential? value) (into [] (map as-cljs-number) value)
    :else value))

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
                      (= (count value)
                         (count (into #{} (map as-cljs-number) value))))
    (map? value) (every? (fn [[k v]]
                           (and (or (portable-keyword? k) (string? k))
                                (portable-edn? v)))
                         value)
    :else false))

(def PortableEdn
  [:fn {:error/message
        "value must be portable EDN: finite safe numbers, #inst, and no functions"}
   portable-edn?])
