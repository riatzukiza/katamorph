(ns katamorph.access.registry
  "Schemas for the access shapes, keyed by manifest kind.

   Later grants, messages, and mailboxes join this map. schema.cljs merges it
   into the public registry so a namespace file and a JVM caller share one
   contract."
  (:require [katamorph.resource.shape :as resource]))

(def registry
  {:resource resource/ResourceContract})
