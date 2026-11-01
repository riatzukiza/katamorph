(ns katamorph.resource.shape-test
  #?(:clj
     (:require [clojure.test :refer [deftest is testing]]
               [katamorph.edn.sorted :as sorted]
               [katamorph.resource.law :as law]
               [katamorph.resource.shape :as shape])
     :cljs
     (:require [cljs.test :refer [deftest is testing]]
               [katamorph.edn.sorted :as sorted]
               [katamorph.resource.law :as law]
               [katamorph.resource.shape :as shape])))

(def archive
  {:resource/id "drive:folder:openplanner-archive"
   :resource/kind :drive/folder
   :resource/owner "did:web:axxium.promethean.rest"
   :resource/store :axxium/drive
   :resource/location "drive://err/OpenPlanner-Archive"
   :resource/metadata {:append-only true :format :edn}})

(deftest a-located-resource-is-admitted
  (is (true? (law/admitted? archive)))
  (is (= archive (sorted/round-trip archive)))
  (testing "a keyword id and a location map are enough"
    (is (true? (law/admitted?
                {:resource/id :archive
                 :resource/kind :drive/folder
                 :resource/owner "did:plc:err"
                 :resource/location {:bucket "cold"}})))))

(deftest malformed-resources-are-refused
  (testing "identity"
    (is (false? (law/admitted? (dissoc archive :resource/kind))))
    (is (false? (law/admitted? (assoc archive :resource/kind "drive/folder"))))
    (is (false? (law/admitted? (assoc archive :resource/id ""))))
    (is (false? (law/admitted? (assoc archive :resource/owner "https://example.test"))))
    (is (false? (law/admitted? (assoc archive :resource/owner "did:Web:example")))))
  (testing "a resource must say where it lives"
    (is (false? (law/admitted? (dissoc archive :resource/store :resource/location))))
    (is (false? (law/admitted? (-> archive
                                   (dissoc :resource/location)
                                   (assoc :resource/store "")))))
    (is (false? (law/admitted? (-> archive
                                   (dissoc :resource/store)
                                   (assoc :resource/location "")))))
    (is (false? (law/admitted? (assoc archive :resource/location 5)))
        "a store does not excuse a location that is not a string or a map"))
  (testing "metadata stays portable"
    (is (false? (law/admitted? (assoc-in archive [:resource/metadata :fn] (fn [] 1)))))
    (is (false? (law/admitted? (assoc archive :host/fn (fn [] 1))))))
  (testing "did? is a DID, not a DID URL"
    (is (true? (shape/did? "did:web:axxium.promethean.rest")))
    (is (true? (shape/did? "did:web:example.com:user:alice")))
    (is (true? (shape/did? "did:web:example.com%3A3000")))
    (is (true? (shape/did? "did:example:a::b")))
    (is (true? (shape/did? "did:example::abc")))
    (is (true? (shape/did? (str "did:web:" (apply str (repeat 8000 "a"))))))
    (is (false? (shape/did? "did:web:")))
    (is (false? (shape/did? "did:web:example.test#key-1")))
    (is (false? (shape/did? "did:web:example.test?q=1")))
    (is (false? (shape/did? "did:web:example.test/path")))))
