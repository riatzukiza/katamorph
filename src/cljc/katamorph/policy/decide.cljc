(ns katamorph.policy.decide
  "Decide a resource act from grants and policy gates.

   The decision is a pure function of the event log, the gates, the subject,
   and an explicit instant. No covering grant is :block with reason
   :grant/absent. Otherwise the strongest matching gate wins, and no match
   is :allow."
  (:require [katamorph.grant.law :as grant]
            [katamorph.policy.match :as match]))

(defn decide
  "events — grants and revokes in ledger order
   gates — policy-gate maps
   subject — {:actor did :resource/kind :resource/id :action}
   at — the instant the decision is for"
  [{:keys [events gates subject at]}]
  (let [evaluated (grant/effective events at)
        laws (grant/law-ids evaluated)]
    (if (or (contains? laws :revoke/invalid)
            (contains? laws :revoke/duplicate))
      {:action :block
       :reason :revoke/invalid
       :policy nil
       :grant nil
       :matches []}
      (let [allowed (->> (:grants evaluated)
                         (filter (fn [candidate]
                                   (and (= (:actor subject) (:grant/actor candidate))
                                        (= (:resource/id subject) (:grant/resource candidate))
                                        ((set (:grant/actions candidate)) (:action subject)))))
                         vec)
            inline (->> allowed (keep :grant/policy) (filter map?))
            gate-result (match/evaluate-gates (concat gates inline) subject)]
        (if (empty? allowed)
          {:action :block
           :reason :grant/absent
           :policy nil
           :grant nil
           :matches []}
          (assoc gate-result :grant (first allowed)))))))
