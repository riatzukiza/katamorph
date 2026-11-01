(ns katamorph.resource.shape
  "Resource shape: kind, id, owner Did, store or location, metadata.

   Registration follows the manifest grammar. :resource/id registers the
   resource; the other keys stay in the :resource namespace."
  (:require [clojure.string :as str]
            [katamorph.edn.portable :as portable]))

(defn- char-between?
  [lower value upper]
  (and (not (pos? (compare lower value)))
       (not (pos? (compare value upper)))))

(defn- method-char?
  [c]
  (or (char-between? \a c \z)
      (char-between? \0 c \9)))

(defn- hex-char?
  [c]
  (or (char-between? \0 c \9)
      (char-between? \a c \f)
      (char-between? \A c \F)))

(defn- idchar-end
  "Index after one DID idchar, or nil. A percent sign consumes two hex digits."
  [value index]
  (let [n (count value)]
    (when (< index n)
      (let [c (nth value index)]
        (cond
          (or (char-between? \A c \Z)
              (char-between? \a c \z)
              (char-between? \0 c \9)
              (contains? #{\. \- \_} c))
          (inc index)

          (and (= c \%)
               (< (+ index 2) n)
               (hex-char? (nth value (inc index)))
               (hex-char? (nth value (+ index 2))))
          (+ index 3))))))

(defn did?
  "A DID, not a DID URL. The scan is linear.

   The method is lowercase. The method-specific id is idchar segments
   separated by colons. An earlier segment may be empty (`did:example::abc`).
   The last segment is not. Query, fragment, and slash are not idchar.
   Katamorph does not resolve the DID."
  [value]
  (and (string? value)
       (str/starts-with? value "did:")
       (let [n (count value)
             method-end (loop [index 4]
                          (if (and (< index n) (method-char? (nth value index)))
                            (recur (inc index))
                            index))]
         (and (> method-end 4)
              (< method-end (dec n))
              (= (nth value method-end) \:)
              (loop [index (inc method-end)]
                (let [segment-end (loop [at index]
                                    (if-let [next-at (idchar-end value at)]
                                      (recur next-at)
                                      at))]
                  (cond
                    (= segment-end n) (> segment-end index)
                    (and (< segment-end n) (= (nth value segment-end) \:))
                    (recur (inc segment-end))
                    :else false)))))))

(def Did
  [:fn {:error/message "expected a did: URI"}
   did?])

(defn resource-id?
  [value]
  (or (portable/portable-keyword? value)
      (and (string? value)
           (not (str/blank? value)))))

(defn location-value?
  "A location is a non-blank string or a non-empty portable map."
  [location]
  (or (and (string? location) (not (str/blank? location)))
      (and (map? location) (seq location))))

(defn located?
  "A resource names where it lives: a store id, a location, or both.

   When :resource/location is present it must be a location value, even if a
   store is also named."
  [resource]
  (let [store (:resource/store resource)
        store? (or (keyword? store)
                   (and (string? store) (not (str/blank? store))))
        location? (contains? resource :resource/location)]
    (and (or (not location?) (location-value? (:resource/location resource)))
         (or store? (and location? (location-value? (:resource/location resource)))))))

(def ResourceContract
  [:and
   [:map {:closed false}
    [:resource/id [:fn {:error/message "resource id must be a non-blank string or keyword"}
                   resource-id?]]
    [:resource/kind :keyword]
    [:resource/owner Did]
    [:resource/store {:optional true} [:or :keyword :string]]
    [:resource/location {:optional true}
     [:or :string [:map-of [:or :keyword :string] portable/PortableEdn]]]
    [:resource/metadata {:optional true}
     [:map-of [:or :keyword :string] portable/PortableEdn]]
    [:contract/kind {:optional true} [:= :resource]]
    [:contract/id {:optional true} [:or :string :keyword]]]
   [:fn {:error/message "a resource names :resource/store or :resource/location"}
    located?]
   [:fn {:error/message "a resource must be portable EDN"}
    portable/portable-edn?]])
