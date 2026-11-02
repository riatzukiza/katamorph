(ns katamorph.policy.match-test
  #?(:clj
     (:require [clojure.test :refer [deftest is testing]]
               [katamorph.policy.decide :as decide]
               [katamorph.policy.match :as match]
               [katamorph.policy.shape :as shape]
               [katamorph.schema.core :as schema])
     :cljs
     (:require [cljs.test :refer [deftest is testing]]
               [katamorph.policy.decide :as decide]
               [katamorph.policy.match :as match]
               [katamorph.policy.shape :as shape]
               [katamorph.schema.core :as schema])))

(def put-gate
  {:policy/match {:resource/kind :drive/folder
                  :resource/id "drive:folder:openplanner-archive"
                  :action :drive/put}
   :policy/action :note
   :policy/reason "recorded"})

(def delete-gate
  {:policy/match {:resource/kind :drive/folder
                  :action :drive/delete}
   :policy/action :block
   :policy/reason "create-only"})

(def tool-gate
  {:policy/match {:tool/name "exec"}
   :policy/action :block
   :policy/reason "no exec"})

(def subject
  {:actor "did:web:openplanner-pager.promethean.rest"
   :resource/kind :drive/folder
   :resource/id "drive:folder:openplanner-archive"
   :action :drive/put})

(def grant
  {:grant/id :pager
   :grant/actor "did:web:openplanner-pager.promethean.rest"
   :grant/resource "drive:folder:openplanner-archive"
   :grant/actions [:drive/put :drive/list]
   :grant/at #inst "2026-11-01T00:00:00.000Z"})

(deftest tool-call-matching-is-unchanged
  (is (true? (match/policy-matches? tool-gate {:tool/name "exec"})))
  (is (false? (match/policy-matches? tool-gate {:tool/name "read"})))
  (is (true? (match/policy-matches?
              {:policy/match {:tool/params {:cmd (fn [v] (= v "ls"))}}}
              {:tool/params {:cmd "ls"}})))
  (is (true? (match/policy-matches? {:policy/match {}} {:tool/name "anything"})))
  (is (= :block (:action (match/evaluate-gates [tool-gate] {:tool/name "exec"}))))
  (is (= :allow (:action (match/evaluate-gates [tool-gate] {:tool/name "read"}))))
  (is (= :allow (:action (match/evaluate-gates [{:policy/match "x" :policy/action :block}]
                                               {:tool/name "exec"})))
      "a non-map match does not throw and does not match")
  (let [result (match/evaluate-gates [{:policy/match {}
                                               :policy/action :deny
                                               :policy/reason "no such action"}]
                                              {:tool/name "exec"})]
    (is (= :block (:action result)) "an unknown gate action fails closed")
    (is (= "no such action" (:reason result))
        "the winning gate is the one whose action normalizes to the result")))

(deftest resource-and-action-match
  (is (true? (match/policy-matches? put-gate subject)))
  (is (false? (match/policy-matches? put-gate (assoc subject :action :drive/list))))
  (is (false? (match/policy-matches? put-gate (assoc subject :resource/id "other"))))
  (is (true? (match/policy-matches? delete-gate (assoc subject :action :drive/delete))))
  (testing "a tool gate does not match a resource act, and the reverse"
    (is (false? (match/policy-matches? tool-gate subject)))
    (is (false? (match/policy-matches? put-gate {:tool/name "exec"}))))
  (testing "an unknown match key still matches nothing"
    (is (false? (match/policy-matches?
                 {:policy/match {:resource/path "secret"}}
                 subject))))
  (is (:ok (schema/validate {:policy-match shape/PolicyMatch}
                            :policy-match
                            (:policy/match put-gate))))
  (is (:ok (schema/validate {:policy-match shape/PolicyMatch}
                            :policy-match
                            (:policy/match tool-gate)))))

(deftest decide-is-a-pure-function-of-at
  (let [at #inst "2026-11-02T00:00:00.000Z"
        allowed (decide/decide {:events [grant]
                                :gates [put-gate]
                                :subject subject
                                :at at})
        blocked (decide/decide {:events [grant]
                                :gates [{:policy/match {:resource/kind :drive/folder
                                                        :action :drive/put}
                                         :policy/action :block
                                         :policy/reason "stopped"}]
                                :subject subject
                                :at at})
        ungranted (decide/decide {:events [grant]
                                  :gates [delete-gate]
                                  :subject (assoc subject :action :drive/delete)
                                  :at at})
        expired (decide/decide {:events [(assoc grant :grant/expires
                                                #inst "2026-11-01T12:00:00.000Z")]
                                :gates []
                                :subject subject
                                :at at})]
    (is (= :note (:action allowed)))
    (is (= "recorded" (:reason allowed)))
    (is (= :pager (get-in allowed [:grant :grant/id])))
    (is (= allowed (decide/decide {:events [grant] :gates [put-gate]
                                   :subject subject :at at})))
    (is (= :block (:action blocked)))
    (is (= "stopped" (:reason blocked)))
    (is (= :grant/absent (:reason ungranted)))
    (is (= :block (:action expired)))
    (is (= :grant/absent (:reason expired)))))

(deftest a-dropped-revoke-does-not-leave-the-grant-in-force
  (let [bad {:revoke/id :bad
             :revoke/actor "not-a-did"
             :revoke/resource "drive:folder:openplanner-archive"
             :revoke/at #inst "2026-11-01T00:00:00.000Z"}
        result (decide/decide {:events [grant bad]
                               :gates []
                               :subject subject
                               :at #inst "2026-11-02T00:00:00.000Z"})]
    (is (= :block (:action result)))
    (is (= :revoke/invalid (:reason result)))))
