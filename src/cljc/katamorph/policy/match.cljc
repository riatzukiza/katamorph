(ns katamorph.policy.match
  "Flat policy match for tool calls and resource acts.

   :tool/name and :tool/params keep the gate's original behavior, including
   function values in params. :resource/kind, :resource/id, and :action match
   by equality. Any other key matches nothing.")

(defn- match-params?
  "Check if all expected param key-value pairs match the actual params.
   If a value is a fn, it's called with the actual value (predicate match).
   Otherwise, exact equality is required. A non-map does not match."
  [expected actual]
  (and (map? expected)
       (map? actual)
       (every? (fn [[k v]]
                 (let [actual-val (get actual k)]
                   (if (fn? v)
                     (v actual-val)
                     (= v actual-val))))
               expected)))

(def resource-match-keys
  #{:resource/kind :resource/id :action})

(defn policy-matches?
  "Does a policy-gate match a tool call or a resource act?

   tool call — {:tool/name string :tool/params map}
   resource act — {:resource/kind keyword :resource/id id :action keyword}"
  [policy subject]
  (let [match (:policy/match policy {})]
    (and (map? match)
         (every? (fn [[k v]]
              (cond
                (= k :tool/name)
                (= v (:tool/name subject))

                (= k :tool/params)
                (match-params? v (:tool/params subject {}))

                (contains? resource-match-keys k)
                (= v (get subject k))

                :else
                false))
            match))))

(def ^:private action-severity
  "Numeric severity for policy actions. Higher = stronger."
  {:block 3
   :warn  2
   :note  1
   :allow 0})

(defn strongest-action
  "Given a seq of action keywords, return the one with highest severity."
  [actions]
  (let [n (apply max 0 (map #(get action-severity % 3) actions))]
    (some (fn [[k v]] (when (= v n) k)) action-severity)))

(defn- policy-active?
  "Is this policy gate still within its TTL window?"
  [policy now-ms loaded-at]
  (let [ttl (:policy/ttl-ms policy)]
    (if (and ttl now-ms loaded-at)
      (< (- now-ms loaded-at) ttl)
      true)))

(defn evaluate-gates
  "Evaluate policy-gate contracts against a tool call or a resource act.

   Returns:
     {:action  :block | :warn | :note | :allow
      :reason  string?
      :policy  matching-policy-or-nil
      :matches vector-of-all-matching-policies}

   When multiple policies match, the strongest action wins.
   No match is :allow."
  ([gates subject]
   (evaluate-gates gates subject nil nil))
  ([gates subject now-ms loaded-at]
   (let [active (filter #(policy-active? % now-ms loaded-at) gates)
         matches (filter #(policy-matches? % subject) active)]
     (if (empty? matches)
       {:action :allow :reason nil :policy nil :matches []}
       (let [action (strongest-action (map :policy/action matches))
             winner (first (filter #(= action (:policy/action %)) matches))]
         {:action action
          :reason (:policy/reason winner)
          :policy winner
          :matches (vec matches)})))))
