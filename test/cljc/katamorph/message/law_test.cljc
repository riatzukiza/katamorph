(ns katamorph.message.law-test
  #?(:clj
     (:require [clojure.test :refer [deftest is testing]]
               [clio.law.event :as clio-event]
               [clio.shape.canonical :as canonical]
               [clio.shape.edn :as clio-edn]
               [katamorph.edn.sorted :as sorted]
               [katamorph.message.law :as law]
               [katamorph.message.shape :as shape])
     :cljs
     (:require [cljs.test :refer [deftest is testing]]
               [clio.law.event :as clio-event]
               [clio.shape.canonical :as canonical]
               [clio.shape.edn :as clio-edn]
               [katamorph.edn.sorted :as sorted]
               [katamorph.message.law :as law]
               [katamorph.message.shape :as shape])))

(def err "did:web:err.promethean.rest")
(def pager "did:web:openplanner-pager.promethean.rest")

(def page-written
  {:message/id :page-written
   :event/id "6f1c1b2a-7c3d-4e5f-8a90-1b2c3d4e5f60"
   :event/stream "mbox/did:web:err.promethean.rest"
   :event/seq 1
   :event/causes []
   :event/type :archive/page-written
   :event/actor pager
   :event/at #inst "2026-11-01T22:00:00.000Z"
   :event/body {:resource/id "drive:folder:openplanner-archive"}
   :msg/to err
   :msg/channel :notices})

(def other
  (assoc page-written
         :message/id :other
         :event/id "7a1c1b2a-7c3d-4e5f-8a90-1b2c3d4e5f61"
         :event/seq 2
         :msg/to pager))

(def notices
  {:channel/id :notices
   :channel/owner err
   :channel/store :axxium/mbox
   :channel/metadata {:kind :msg/channel}})

(deftest message-identity-matches-clio
  (is (true? (:ok (law/explain-message page-written))))
  (is (= (clio-event/event-identity-valid? page-written)
         (shape/event-identity-valid? page-written)))
  (is (= page-written (sorted/round-trip page-written)))
  (is (= page-written (clio-edn/read-one (sorted/export-edn page-written))))
  (is (= (canonical/canonical-form page-written)
         (canonical/canonical-form (sorted/round-trip page-written))))
  (testing "clio refuses the same broken identities we refuse"
    (doseq [broken [(assoc page-written :event/id "not-a-uuid")
                    (assoc page-written :event/stream "  ")
                    (assoc page-written :event/seq 0)
                    (assoc page-written :event/causes [(:event/id page-written)])
                    (assoc page-written :event/causes ["6f1c1b2a-7c3d-4e5f-8a90-1b2c3d4e5f60"
                                                      "6f1c1b2a-7c3d-4e5f-8a90-1b2c3d4e5f60"])]]
      (is (= (clio-event/event-identity-valid? broken)
             (shape/event-identity-valid? broken)))
      (is (false? (shape/event-identity-valid? broken)))
      (is (false? (:ok (law/explain-message broken))))))
  (is (false? (:ok (law/explain-message (dissoc page-written :msg/to)))))
  (is (false? (:ok (law/explain-message (assoc page-written :msg/to "not-a-did"))))))

(deftest mailbox-is-the-fold-of-addressed-messages
  (let [log [page-written other page-written]]
    (is (= [page-written page-written] (law/mailbox err log)))
    (is (= [other] (law/mailbox pager log)))
    (is (= [] (law/mailbox "did:plc:nobody" log)))
    (is (= (law/mailbox err (into [page-written] [other page-written]))
           (into (law/mailbox err [page-written])
                 (law/mailbox err [other page-written]))))))

(deftest channel-projects-to-a-msg-channel-resource
  (is (true? (law/channel-admitted? notices)))
  (is (= {:resource/id :notices
          :resource/kind :msg/channel
          :resource/owner err
          :resource/store :axxium/mbox
          :resource/metadata {:kind :msg/channel}}
         (law/channel-resource notices)))
  (is (false? (:ok (law/explain-channel (dissoc notices :channel/store)))))
  (is (true? (:ok (law/explain-mailbox {:mailbox/id :err
                                        :mailbox/did err
                                        :mailbox/channel :notices}))))
  (is (= notices (clio-edn/read-one (sorted/export-edn notices))))
  (is (= (canonical/canonical-form notices)
         (canonical/canonical-form (sorted/round-trip notices)))))
