(ns katamorph.edn.sorted-test
  #?(:clj
     (:require [clojure.test :refer [deftest is]]
               [katamorph.edn.sorted :as sorted])
     :cljs
     (:require [cljs.test :refer [deftest is]]
               [katamorph.edn.sorted :as sorted])))

(def sample
  {:resource/id "drive:folder:openplanner-archive"
   :resource/kind :drive/folder
   :resource/owner "did:web:axxium.promethean.rest"
   :resource/store :axxium/drive
   :resource/metadata {:append-only true
                       :tags #{:edn :archive}}
   :resource/location {:path "OpenPlanner Archive" :n 1}})

(deftest export-ignores-map-order
  (is (= (sorted/export-edn {:b 1 :a {:d 3 :c 2}})
         (sorted/export-edn {:a {:c 2 :d 3} :b 1}))))

(deftest admitted-sample-round-trips
  (is (= sample (sorted/round-trip sample))))

(deftest export-ignores-print-truncation
  (binding [*print-length* 1
            *print-level* 1
            *print-readably* false]
    (is (= (sorted/export-edn #{[1 2] [1 3]})
           (sorted/export-edn #{[1 3] [1 2]})))
    (is (= #{[1 2] [1 3]} (set (sorted/round-trip #{[1 2] [1 3]}))))
    (is (= {:a "x"} (sorted/round-trip {:a "x"})))
    (is (= #{1 "1"} (set (sorted/round-trip #{1 "1"})))))
  (binding [*print-dup* true
            *print-namespace-maps* true]
    (is (= {:a "x"} (sorted/round-trip {:a "x"})))))
