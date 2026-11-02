(ns katamorph.edn.clio-round-trip-test
  "Value round-trip through clio's merged canonical EDN.

   canonical-form is not invertible. The check is: export-edn, read that text
   with clio.shape.edn/read-one, and require canonical-form to be unchanged.
   import-edn remains the local reader until clio ships a value-level import."
  #?(:clj
     (:require [clojure.test :refer [deftest is]]
               [clio.shape.canonical :as canonical]
               [clio.shape.edn :as clio-edn]
               [katamorph.edn.sorted :as sorted]
               [katamorph.resource.law :as law])
     :cljs
     (:require [cljs.test :refer [deftest is]]
               [clio.shape.canonical :as canonical]
               [clio.shape.edn :as clio-edn]
               [katamorph.edn.sorted :as sorted]
               [katamorph.resource.law :as law])))

(def archive
  {:resource/id "drive:folder:openplanner-archive"
   :resource/kind :drive/folder
   :resource/owner "did:web:axxium.promethean.rest"
   :resource/store :axxium/drive
   :resource/location {:path "OpenPlanner Archive" :generation 1}
   :resource/metadata {:append-only true}})

(deftest resource-round-trips-through-clio
  (is (true? (law/admitted? archive)))
  (let [text (sorted/export-edn archive)
        imported (clio-edn/read-one text)]
    (is (= archive imported))
    (is (= archive (sorted/import-edn text)))
    (is (= (canonical/canonical-form archive)
           (canonical/canonical-form imported)))
    (is (= (canonical/canonical-form archive)
           (clio-edn/read-one (canonical/canonical-edn archive))))))
