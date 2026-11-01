(ns katamorph.examples-test
  (:require [cljs.test :refer [deftest is testing]]
            [katamorph.examples.runner :as examples]))

(deftest examples-are-valid-contract-manifests
  (testing "hello-world"
    (is (= {:path "examples/hello-world.edn"
            :namespace :hello
            :resources 2
            :kinds [:action :trigger]}
           (examples/example-report "examples/hello-world.edn"))))
  (testing "portable host runtime declarations"
    (is (= {:path "examples/host-runtime.edn"
            :namespace :open-hax
            :resources 5
            :kinds [:agent :mcp-server :model :model-family :provider]}
           (-> (examples/example-report "examples/host-runtime.edn")
               (update :kinds #(vec (sort %))))))))

(deftest archive-access-resource-is-a-valid-manifest
  (is (= {:path "examples/archive-access.edn"
          :namespace :openplanner.archive
          :resources 3
          :kinds [:resource :grant :revoke]}
         (examples/example-report "examples/archive-access.edn"))))

(deftest ^:async hello-world-resolves-and-executes-an-edn-action
  (is (= {:message "Hello, Katamorph!"
          :handled-by :demo/greet}
         (await (examples/run-hello!)))))
