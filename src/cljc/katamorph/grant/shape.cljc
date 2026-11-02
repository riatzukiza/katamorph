(ns katamorph.grant.shape
  "Grant and revoke shapes.

   A grant is an allowance: actor Did, resource, actions, optional policy,
   optional issued-at, optional expiry, optional parent. A revoke names the
   same coordinates plus the instant it takes effect. :revoke/expires selects
   grants with that expiry; it does not restore authority."
  (:require [katamorph.edn.portable :as portable]
            [katamorph.policy.shape :as policy]
            [katamorph.resource.shape :as resource]))

(def Id
  [:fn {:error/message "id must be a non-blank string or keyword"}
   resource/resource-id?])

(defn- unique-keywords?
  [actions]
  (and (vector? actions)
       (every? keyword? actions)
       (= (count actions) (count (set actions)))))

(def Actions
  [:and
   [:vector :keyword]
   [:fn {:error/message "actions must be unique keywords"}
    unique-keywords?]])

(def NonEmptyActions
  [:and
   Actions
   [:fn {:error/message "a grant names at least one action"}
    (fn [actions]
      (and (vector? actions) (pos? (count actions))))]])

(def PolicyRef
  "A gate id, or an inline gate. decide evaluates a map. A keyword or string
   is a reference the caller resolves into the gate list."
  [:or :keyword :string
   [:map
    [:policy/match policy/PolicyMatch]
    [:policy/action [:enum :block :warn :note :allow]]
    [:policy/reason {:optional true} :string]
    [:policy/ttl-ms {:optional true} :int]]])

(def Instant
  [:fn {:error/message "expected a portable #inst"}
   portable/portable-instant?])

(def GrantContract
  [:and
   [:map {:closed false}
    [:grant/id Id]
    [:grant/actor resource/Did]
    [:grant/resource Id]
    [:grant/actions NonEmptyActions]
    [:grant/policy {:optional true} PolicyRef]
    [:grant/at {:optional true} Instant]
    [:grant/expires {:optional true} Instant]
    [:grant/from {:optional true} Id]
    [:contract/kind {:optional true} [:= :grant]]
    [:contract/id {:optional true} [:or :string :keyword]]]
   [:fn {:error/message "a grant must be portable EDN"}
    portable/portable-edn?]])

(def RevokeContract
  [:and
   [:map {:closed false}
    [:revoke/id Id]
    [:revoke/actor resource/Did]
    [:revoke/resource Id]
    [:revoke/actions {:optional true} Actions]
    [:revoke/policy {:optional true} PolicyRef]
    [:revoke/at Instant]
    [:revoke/expires {:optional true} Instant]
    [:contract/kind {:optional true} [:= :revoke]]
    [:contract/id {:optional true} [:or :string :keyword]]]
   [:fn {:error/message "a revoke must be portable EDN"}
    portable/portable-edn?]])
