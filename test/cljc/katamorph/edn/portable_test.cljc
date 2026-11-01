(ns katamorph.edn.portable-test
  #?(:clj
     (:require [clojure.test :refer [deftest is]]
               [katamorph.edn.portable :as portable])
     :cljs
     (:require [cljs.test :refer [deftest is]]
               [katamorph.edn.portable :as portable])))

(deftest portable-scalars-cross-the-boundary
  (is (portable/portable-edn? nil))
  (is (portable/portable-edn? true))
  (is (portable/portable-edn? "archive"))
  (is (portable/portable-edn? :drive/folder))
  (is (portable/portable-keyword? :drive/folder))
  (is (portable/portable-edn? 1))
  (is (portable/portable-edn? 1.5))
  (is (portable/portable-edn? #inst "2026-11-01T00:00:00.000Z")))

(deftest non-portable-values-are-refused
  (is (false? (portable/portable-edn? ##NaN)))
  (is (false? (portable/portable-edn? ##Inf)))
  (is (false? (portable/portable-edn? ##-Inf)))
  (is (false? (portable/portable-edn? (fn [] 1))))
  (is (false? (portable/portable-edn? 'symbol)))
  (is (false? (portable/portable-edn? #inst "1500-01-01T00:00:00.000Z"))))

#?(:clj
   (deftest jvm-only-exact-numbers-are-refused
     (is (false? (portable/portable-edn? 1/2)))
     (is (false? (portable/portable-edn? 1.5M)))))

(deftest nested-edn-must-be-portable-throughout
  (is (portable/portable-edn? {:a [1 #{:b}] "c" true}))
  (is (false? (portable/portable-edn? {:a (fn [] 1)})))
  (is (false? (portable/portable-edn? [##NaN]))))

(deftest keywords-must-round-trip-through-edn
  (is (false? (portable/portable-keyword? (keyword "foo bar"))))
  (is (false? (portable/portable-keyword? (keyword "foo" "/bar"))))
  (is (true? (portable/portable-keyword?
              :openplanner.archive/drive:folder:openplanner-archive)))
  (is (false? (portable/portable-edn? {:resource/kind (keyword "foo bar")})))
  (is (false? (portable/portable-edn? {(keyword "foo bar") 1}))))

(deftest integral-values-stay-inside-the-safe-range
  (is (false? (portable/portable-edn? 9007199254740992)))
  (is (true? (portable/portable-edn? 9007199254740991))))

#?(:clj
   (deftest sets-reject-cross-runtime-numeric-twins
     (is (false? (portable/portable-edn? #{1 1.0})))
     (is (true? (portable/portable-edn? #{1 2})))))

#?(:clj
   (deftest timestamps-are-not-portable-instants
     (is (false? (portable/portable-instant? (java.sql.Timestamp. 0))))))
