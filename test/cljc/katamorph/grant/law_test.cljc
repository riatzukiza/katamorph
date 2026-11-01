(ns katamorph.grant.law-test
  #?(:clj
     (:require [clojure.set :as set]
               [clojure.test :refer [deftest is testing]]
               [katamorph.edn.sorted :as sorted]
               [katamorph.grant.law :as law])
     :cljs
     (:require [clojure.set :as set]
               [cljs.test :refer [deftest is testing]]
               [katamorph.edn.sorted :as sorted]
               [katamorph.grant.law :as law])))

(def t0 #inst "2026-11-01T00:00:00.000Z")
(def t1 #inst "2026-12-01T00:00:00.000Z")
(def t2 #inst "2027-01-01T00:00:00.000Z")
(def t3 #inst "2027-06-01T00:00:00.000Z")

(def root
  {:grant/id :pager
   :grant/actor "did:web:openplanner-pager.promethean.rest"
   :grant/resource "drive:folder:openplanner-archive"
   :grant/actions [:drive/list :drive/put]
   :grant/at t0
   :grant/expires t2})

(def child
  {:grant/id :pager-put
   :grant/actor "did:web:worker.promethean.rest"
   :grant/resource "drive:folder:openplanner-archive"
   :grant/actions [:drive/put]
   :grant/from :pager
   :grant/at t0
   :grant/expires t1})

(defn- actions-of
  [result id]
  (set (:grant/actions (first (filter #(= id (:grant/id %)) (:grants result))))))

(deftest root-grant-is-live-inside-its-window
  (let [before (law/effective [root] #inst "2026-10-01T00:00:00.000Z")
        during (law/effective [root] t1)
        after (law/effective [root] t2)]
    (is (empty? (:grants before)))
    (is (= #{:drive/list :drive/put} (actions-of during :pager)))
    (is (empty? (:grants after)) "expiry is exclusive")
    (is (= during (law/effective [root] t1)) "evaluation depends only on at")))

(deftest derived-grants-only-narrow
  (is (empty? (law/law-ids (law/index [root child]))))
  (testing "a wider action is refused and ignored by the fold"
    (let [wide (assoc child :grant/actions [:drive/put :drive/delete])
          result (law/index [root wide])]
      (is (= #{:grant/widens} (law/law-ids result)))
      (is (nil? (get (:grants result) :pager-put)))))
  (testing "a later expiry extends the parent"
    (is (= #{:grant/extends}
           (law/law-ids (law/index [root (assoc child :grant/expires t3)])))))
  (testing "an open expiry extends a bounded parent"
    (is (= #{:grant/extends}
           (law/law-ids (law/index [root (dissoc child :grant/expires)])))))
  (testing "a missing parent and a different resource are refused"
    (is (= #{:grant/dangling} (law/law-ids (law/index [child]))))
    (is (= #{:grant/resource-drift}
           (law/law-ids (law/index [root (assoc child :grant/resource "other")]))))))

(deftest revocation-is-monotone-and-does-not-lapse
  (let [revoke {:revoke/id :stop-put
                :revoke/actor (:grant/actor root)
                :revoke/resource (:grant/resource root)
                :revoke/actions [:drive/put]
                :revoke/expires t2
                :revoke/at t1
                :revoke/policy :archive/page-out}
        before (law/effective [root] t0)
        at-revoke (law/effective [root revoke] t1)
        later (law/effective [root revoke] #inst "2026-12-15T00:00:00.000Z")]
    (is (= #{:drive/list :drive/put} (actions-of before :pager)))
    (is (= #{:drive/list} (actions-of at-revoke :pager)))
    (is (= #{:drive/list} (actions-of later :pager))
        "revoke/expires does not restore the action")
    (is (set/subset? (actions-of at-revoke :pager) (actions-of before :pager)))
    (testing "appending the revoke never increases a grant's residual"
      (let [early (assoc revoke :revoke/id :stop-early :revoke/at t0)
            with (law/effective [root child early] t0)
            without (law/effective [root child] t0)]
        (is (set/subset? (actions-of with :pager) (actions-of without :pager)))
        (is (set/subset? (actions-of with :pager-put) (actions-of without :pager-put)))
        (is (not= (actions-of with :pager) (actions-of without :pager)))))
    (testing "a new root grant is a new allowance, not an edit of the revoked one"
      (let [again (assoc root :grant/id :pager-again :grant/at t1)
            result (law/effective [root revoke again] t1)]
        (is (= #{:drive/list} (actions-of result :pager)))
        (is (= #{:drive/list :drive/put} (actions-of result :pager-again)))))))

(deftest revocation-cascades-along-from
  (let [revoke {:revoke/id :stop-put
                :revoke/actor (:grant/actor root)
                :revoke/resource (:grant/resource root)
                :revoke/actions [:drive/put]
                :revoke/at t0}
        result (law/effective [root child revoke] t0)]
    (is (= #{:drive/list} (actions-of result :pager)))
    (is (empty? (actions-of result :pager-put))
        "the child cannot keep an action the parent lost")))

(deftest revoke-expiry-selects-one-allowance
  (let [other (assoc root :grant/id :other :grant/expires t3)
        revoke {:revoke/id :stop-one
                :revoke/actor (:grant/actor root)
                :revoke/resource (:grant/resource root)
                :revoke/actions [:drive/put]
                :revoke/expires t2
                :revoke/at t0}
        result (law/effective [root other revoke] t1)]
    (is (= #{:drive/list} (actions-of result :pager)))
    (is (= #{:drive/list :drive/put} (actions-of result :other)))))

(deftest malformed-events-are-refused
  (is (false? (:ok (law/explain-grant (dissoc root :grant/actor)))))
  (is (false? (:ok (law/explain-grant (assoc root :grant/actions [:drive/put :drive/put])))))
  (is (false? (:ok (law/explain-grant (assoc root :grant/actor "not-a-did")))))
  (is (false? (:ok (law/explain-revoke {:revoke/id :x
                                        :revoke/actor (:grant/actor root)
                                        :revoke/resource (:grant/resource root)}))))
  (is (= #{:grant/duplicate}
         (law/law-ids (law/index [root root]))))
  (is (= #{:grant/window}
         (law/law-ids (law/index [(assoc root :grant/at t2 :grant/expires t0)]))))
  (is (= #{:grant/unknown-event}
         (law/law-ids (law/index [{:message/id :nope}]))))
  (is (= root (sorted/round-trip root))))
