(ns katamorph.policy.gate
  "Flat policy gate evaluation for tool calls and resource acts.

   Heritage: eta-mu.extensions.contract-runtime-v2.core

   Policy gate contracts use :policy/match maps. A match may name a tool call
   (:tool/name, :tool/params) or a resource act (:resource/kind, :resource/id,
   :action). :policy/action is the outcome (:block, :warn, :note, :allow).

   This is simpler than the tree-shaped policy evaluator — it's designed
   for fast, flat lookups. The match itself lives in katamorph.policy.match
   so the JVM and ClojureScript run the same function."
  (:require [katamorph.policy.match :as match]))

(defn policy-matches?
  [policy subject]
  (match/policy-matches? policy subject))

(defn strongest-action
  [actions]
  (match/strongest-action actions))

(defn evaluate-gates
  ([gates subject]
   (match/evaluate-gates gates subject))
  ([gates subject now-ms loaded-at]
   (match/evaluate-gates gates subject now-ms loaded-at)))
