(ns katamorph.resource.shape
  "Resource shape: kind, id, owner Did, store or location, metadata.

   Registration follows the manifest grammar. :resource/id registers the
   resource; the other keys stay in the :resource namespace."
  (:require [clojure.string :as str]
            [katamorph.edn.portable :as portable]))

(def did-pattern
  #"did:[a-z0-9]+:\S+")

(defn did?
  "A did: URI. The method is lowercase. Katamorph does not resolve it."
  [value]
  (boolean (and (string? value) (re-matches did-pattern value))))

(def Did
  [:fn {:error/message "expected a did: URI"}
   did?])

(defn resource-id?
  [value]
  (or (and (keyword? value)
           (not (str/blank? (name value))))
      (and (string? value)
           (not (str/blank? value)))))

(defn located?
  "A resource names where it lives: a store id, a location, or both."
  [resource]
  (let [store (:resource/store resource)
        location (:resource/location resource)]
    (or (keyword? store)
        (and (string? store) (not (str/blank? store)))
        (and (string? location) (not (str/blank? location)))
        (and (map? location) (seq location)))))

(def ResourceContract
  [:and
   [:map {:closed false}
    [:resource/id [:fn {:error/message "resource id must be a non-blank string or keyword"}
                   resource-id?]]
    [:resource/kind :keyword]
    [:resource/owner Did]
    [:resource/store {:optional true} [:or :keyword :string]]
    [:resource/location {:optional true} portable/PortableEdn]
    [:resource/metadata {:optional true}
     [:map-of [:or :keyword :string] portable/PortableEdn]]
    [:contract/kind {:optional true} [:= :resource]]
    [:contract/id {:optional true} [:or :string :keyword]]]
   [:fn {:error/message "a resource names :resource/store or :resource/location"}
    located?]
   [:fn {:error/message "a resource must be portable EDN"}
    portable/portable-edn?]])
