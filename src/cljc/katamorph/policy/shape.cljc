(ns katamorph.policy.shape
  "Flat policy match. Tool-call keys stay optional and unchanged.
   Resource keys are optional too, so a gate written for a tool call still
   validates, and a gate may name a resource kind, a resource id, and an action.")

(def PolicyMatch
  [:map
   [:tool/name {:optional true} :string]
   [:tool/params {:optional true} [:map-of :keyword :any]]
   [:resource/kind {:optional true} :keyword]
   [:resource/id {:optional true} [:or :string :keyword]]
   [:action {:optional true} :keyword]])
