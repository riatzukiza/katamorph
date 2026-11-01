(ns katamorph.policy.decide
  "Decide a resource act from grants and policy gates.

   The decision is a pure function of the event log, the gates, the subject,
   and an explicit instant. No covering grant is :block with reason
   :grant/absent. Otherwise the strongest matching gate wins, and no match
   is :allow."
  (:require [katamorph.grant.law :as grant]
            [katamorph.policy.match :as match]))

(defn- covering
  [events subject at]
  (let [actor (:actor subject)
        resource (:resource/id subject)
        action (:action subject)]
    (->> (:grants (grant/effective events at))
         (filter (fn [allowed]
                   (and (= actor (:grant/actor allowed))
                        (= resource (:grant/resource allowed))
                        ((set (:grant/actions allowed)) action))))
         vec)))

(defn decide
  "events — grants and revokes in ledger order
   gates — policy-gate maps
   subject — {:actor did :resource/kind :resource/id :action}
   at — the instant the decision is for"
  [{:keys [events gates subject at]}]
  (let [allowed (covering events subject at)
        inline (->> allowed (keep :grant/policy) (filter map?))
        gate-result (match/evaluate-gates (concat gates inline) subject)]
    (if (empty? allowed)
      {:action :block
       :reason :grant/absent
       :policy nil
       :grant nil
       :matches []}
      (assoc gate-result :grant (first allowed)))))
