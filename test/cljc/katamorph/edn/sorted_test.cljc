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
