(ns katamorph.grant.law
  "Pure grant and revoke fold.

   effective is a function of the event log and an explicit instant. It reads
   no clock. Revocation only removes actions, cascades along :grant/from, and
   does not lapse when time moves forward. A derived grant is admitted only
   when it narrows its parent."
  (:require [clojure.set :as set]
            [katamorph.edn.portable :as portable]
            [katamorph.grant.shape :as shape]
            [katamorph.schema.core :as schema]))

(defn grant?
  [event]
  (and (map? event) (contains? event :grant/id)))

(defn revoke?
  [event]
  (and (map? event) (contains? event :revoke/id) (not (grant? event))))

(defn explain-grant
  [grant]
  (schema/validate {:grant shape/GrantContract} :grant grant))

(defn explain-revoke
  [revoke]
  (schema/validate {:revoke shape/RevokeContract} :revoke revoke))

(defn- millis
  [value]
  (when (portable/portable-instant? value)
    (inst-ms value)))

(defn- window-ok?
  "An issued-at must fall strictly before expiry when both are present."
  [grant]
  (let [start (millis (:grant/at grant))
        end (millis (:grant/expires grant))]
    (or (nil? start) (nil? end) (< start end))))

(defn live?
  "True when at is inside the grant window. Expiry is exclusive."
  [grant at]
  (let [now (millis at)
        start (millis (:grant/at grant))
        end (millis (:grant/expires grant))]
    (and (some? now)
         (or (nil? start) (<= start now))
         (or (nil? end) (< now end)))))

(defn- revoke-due?
  [revoke at]
  (let [now (millis at)
        start (millis (:revoke/at revoke))]
    (and (some? now) (some? start) (<= start now))))

(defn- expiry-narrows?
  [parent child]
  (let [parent-end (millis (:grant/expires parent))
        child-end (millis (:grant/expires child))]
    (cond
      (nil? parent-end) true
      (nil? child-end) false
      :else (<= child-end parent-end))))

(defn- children-ids
  [grants id]
  (keep (fn [[child-id grant]]
          (when (= id (:grant/from grant)) child-id))
        grants))

(defn- descendant-ids
  [grants id]
  (loop [pending (vec (children-ids grants id))
         seen []]
    (if (empty? pending)
      seen
      (let [current (peek pending)]
        (recur (into (pop pending) (children-ids grants current))
               (conj seen current))))))

(defn- ban-for
  [revoke grant]
  (let [declared (set (:grant/actions grant))
        named (set (:revoke/actions revoke))]
    (if (seq named)
      (set/intersection named declared)
      declared)))

(defn- same-expiry?
  [revoke grant]
  (let [bound (:revoke/expires revoke)]
    (or (nil? bound)
        (= (millis bound) (millis (:grant/expires grant))))))

(defn- covers?
  [revoke grant]
  (and (= (:revoke/actor revoke) (:grant/actor grant))
       (= (:revoke/resource revoke) (:grant/resource grant))
       (same-expiry? revoke grant)))

(defn- apply-revoke
  [revoked grants revoke targets]
  (reduce
   (fn [revoked grant]
     (if-not (and (contains? targets (:grant/id grant))
                  (covers? revoke grant))
       revoked
       (let [ban (ban-for revoke grant)
             ids (->> (cons (:grant/id grant)
                            (descendant-ids grants (:grant/id grant)))
                      (filter #(contains? targets %)))]
         (reduce (fn [revoked id]
                   (update revoked id (fnil set/union #{}) ban))
                 revoked
                 ids))))
   revoked
   (vals grants)))

(defn- residuals
  [grants revoked at]
  (loop [current (into {}
                       (map (fn [[id grant]]
                              [id (if (live? grant at)
                                    (set (:grant/actions grant))
                                    #{})]))
                       grants)]
    (let [next (into {}
                     (map (fn [[id actions]]
                            (let [grant (get grants id)
                                  banned (get revoked id #{})
                                  parent-id (:grant/from grant)
                                  left (set/difference actions banned)]
                              [id (if parent-id
                                    (set/intersection left (get current parent-id #{}))
                                    left)])))
                     current)]
      (if (= next current)
        current
        (recur next)))))

(defn- present
  [grants residuals]
  (->> residuals
       (keep (fn [[id actions]]
               (when (seq actions)
                 (assoc (get grants id)
                        :grant/actions (vec (sort-by str actions))))))
       (sort-by #(str (:grant/id %)))
       vec))

(defn- law-error
  [law-id id]
  {:law/id law-id :grant/id id})

(defn index
  "Structural admission, independent of the clock.

   Grants are indexed in log order. A derived grant is kept only when it
   narrows a parent already in the log. Unlawful grants are reported and
   omitted, so a later fold cannot treat them as authority."
  [events]
  (loop [remaining (seq events)
         grants {}
         revokes []
         revoke-ids #{}
         errors []]
    (if-not remaining
      {:grants grants :revokes revokes :errors errors}
      (let [event (first remaining)
            rest-events (next remaining)]
        (cond
          (grant? event)
          (let [shape (explain-grant event)
                id (:grant/id event)
                parent-id (:grant/from event)
                parent (get grants parent-id)
                problem (cond
                          (not (:ok shape)) nil
                          (contains? grants id) :grant/duplicate
                          (not (window-ok? event)) :grant/window
                          (and parent-id (nil? parent)) :grant/dangling
                          (and parent (not= (:grant/resource event)
                                            (:grant/resource parent)))
                          :grant/resource-drift
                          (and parent
                               (not (set/subset? (set (:grant/actions event))
                                                 (set (:grant/actions parent)))))
                          :grant/widens
                          (and parent (not (expiry-narrows? parent event)))
                          :grant/extends
                          :else nil)]
            (recur rest-events
                   (if (and (:ok shape) (nil? problem))
                     (assoc grants id event)
                     grants)
                   revokes
                   revoke-ids
                   (cond
                     (not (:ok shape)) (into errors (:errors shape))
                     problem (conj errors (law-error problem id))
                     :else errors)))

          (revoke? event)
          (let [shape (explain-revoke event)
                id (:revoke/id event)
                duplicate? (contains? revoke-ids id)]
            (recur rest-events
                   grants
                   (if (and (:ok shape) (not duplicate?))
                     (conj revokes {:event event :targets (set (keys grants))})
                     revokes)
                   (if (and (:ok shape) (not duplicate?))
                     (conj revoke-ids id)
                     revoke-ids)
                   (cond
                     (not (:ok shape)) (into errors (:errors shape))
                     duplicate? (conj errors {:law/id :revoke/duplicate :revoke/id id})
                     :else errors)))

          :else
          (recur rest-events
                 grants
                 revokes
                 revoke-ids
                 (conj errors {:law/id :grant/unknown-event})))))))

(defn effective
  "Live grant residuals at at. Actions are sorted, grants are sorted by id.

   A revoke with :revoke/at <= at withdraws its actions from every matching
   grant and from every grant derived from those, and keeps them withdrawn at
   every later instant. :revoke/expires matches a grant expiry; it is not a
   lapse."
  [events at]
  (let [{:keys [grants revokes errors]} (index events)
        revoked (reduce (fn [revoked {:keys [event targets]}]
                          (if (revoke-due? event at)
                            (apply-revoke revoked grants event targets)
                            revoked))
                        {}
                        revokes)]
    {:ok (empty? errors)
     :errors errors
     :at at
     :grants (present grants (residuals grants revoked at))}))

(defn law-ids
  [result]
  (set (keep :law/id (:errors result))))
