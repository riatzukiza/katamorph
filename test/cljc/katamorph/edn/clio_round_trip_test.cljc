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

(def grant
  {:grant/id :pager-put
   :grant/actor "did:web:openplanner-pager.promethean.rest"
   :grant/resource "drive:folder:openplanner-archive"
   :grant/actions [:drive/put :drive/list]
   :grant/at #inst "2026-11-01T00:00:00.000Z"
   :grant/expires #inst "2027-01-01T00:00:00.000Z"})

(def revoke
  {:revoke/id :pager-put-stop
   :revoke/actor "did:web:openplanner-pager.promethean.rest"
   :revoke/resource "drive:folder:openplanner-archive"
   :revoke/actions [:drive/put]
   :revoke/at #inst "2026-12-01T00:00:00.000Z"})

(deftest grant-and-revoke-round-trip-through-clio
  (doseq [value [grant revoke]]
    (let [text (sorted/export-edn value)
          imported (clio-edn/read-one text)]
      (is (= value imported))
      (is (= (canonical/canonical-form value)
             (canonical/canonical-form imported))))))
