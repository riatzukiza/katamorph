(ns katamorph.resource.law
  "Admission for a resource. The check is pure: no clock and no I/O."
  (:require [katamorph.resource.shape :as shape]
            [katamorph.schema.core :as schema]))

(def registry
  {:resource shape/ResourceContract})

(defn explain
  "Return the portable validation result for one resource."
  [resource]
  (schema/validate registry :resource resource))

(defn admitted?
  [resource]
  (:ok (explain resource)))
