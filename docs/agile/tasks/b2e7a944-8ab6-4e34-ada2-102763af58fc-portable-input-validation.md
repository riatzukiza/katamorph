---
uuid: b2e7a944-8ab6-4e34-ada2-102763af58fc
title: Validate portable non-step workflow inputs
status: incoming
priority: high
points: 5
labels: katamorph, workflow, portable, validation, planning
---

## Context

[Canonical issue 25](https://github.com/open-hax/katamorph/issues/25) follows the
shape-only input language from PR 17. At main
`fe6017b28baa2d561550dc03768b3dd0da3f1480`, all non-step inputs receive
`:workflow/unsupported-reference`, including compatible concrete literals.
The source-kind grammar is already owned by `katamorph.action.input`.

No board configuration or neighboring cards exist at this baseline. This is a
hand-authored incoming planning artifact, not a claim that Rheos has loaded,
validated, transitioned or made the card ready. Implementation must wait for
qualified planning review and an actual lawful ready transition through Rheos.

## Outcome

Workflow validation classifies every portable input kind, proves the compatibility
it can establish from portable data, and reports why it cannot prove the rest.
It does not resolve an event, workflow value, trigger payload or resource.

## Scope

- Keep the shared input grammar authoritative; validate it before dispatching.
- Preserve the existing typed producer, output declaration and subtype checks for
  `[:step producer output]`, including their deterministic diagnostics.
- Add an optional portable source-contract map keyed by complete canonical event,
  workflow-input, trigger-payload or resource references. Values declare provided
  schemas, not runtime values, handlers or provider identities. Existing two-argument
  `validate-steps` callers remain supported with an empty map.
- For each of those four source kinds, validate the reference shape, look up its
  exact declared contract, then prove provided <: required using the existing
  schema relation. Report missing declaration or unproven compatibility distinctly;
  a declaration does not prove runtime existence or runtime value conformance.
- Check explicit `[:literal value]` and ordinary portable literals at validation
  time against the required input schema. Use the existing portable-value boundary
  and contract machinery, with a reviewed pure schema subset described in the plan.
- Preserve step-only dependency graph edges and scheduling metadata separation.

## Non-goals

No effects, source resolution, provider choice, credentials, clocks, handlers,
agent policy, execution bindings, package-manager changes or new runtime. No
replacement input grammar, schema engine or board authority. Issue 23 is separate.

## Acceptance criteria

1. Event, workflow-input, trigger-payload and resource inputs each have positive
   fixtures with an exact declared compatible contract and negative fixtures for
   missing declarations and incompatible or unprovable contracts. These findings
   carry stable source kind, step identity, input name and original reference.
2. Both literal forms accept compatible portable values and reject incompatible
   ones, including false, nil, empty collections and explicit escaping of a reserved
   vector. Literal support and unsupported schema forms are documented; arbitrary
   predicates, host schemas and unresolved nominal schemas are never executed or
   silently accepted to manufacture a proof.
3. A malformed reserved-head vector fails closed as an invalid reference. It never
   becomes a literal, contributes a graph edge or throws an unstructured error.
   Nonportable values and malformed source declarations fail closed too.
4. Existing unknown consumer/producer, undeclared input/output, missing input,
   duplicate step and cycle laws remain intact. Diagnostics have deterministic
   ordering and defined precedence when multiple defects apply.
5. Shared CLJC fixtures cover every kind and negative mutation under JVM and CLJS.
   The two-argument API preserves existing step behavior; the additive context form
   does not mutate its inputs or read runtime state.
6. From a cold isolated checkout at one exact implementation revision, run JVM and
   CLJS suites, lint, examples and optimized build. Record native workflow/run IDs,
   actual test/assertion totals and append-only receipts; missing evidence stays
   missing rather than passing.
7. Before implementation, native planning review settles the proposed declaration
   API, literal schema subset, diagnostic names/precedence and five-point estimate;
   Rheos performs and records the lawful ready transition. No auto-merge is enabled.

## Verification

The accompanying [source assessment](../../notes/portable-input-validation-plan.md)
records the exact baseline and current boundaries. The attempted direct NBB probe
could not load `malli.core`; it is unavailable execution evidence, not a test pass.
Future implementation verification uses the repository's declared runtimes and
private dependency/cache/compiler paths. Do not attach to a shared Shadow daemon.

## Risks

Shape validity is not value compatibility. Declared source schemas are conditional
assumptions, not observations of runtime data. Literal validation must not evaluate
user predicates. Diagnostics may change callers' expectations, so their compatibility
boundary needs review. Missing board configuration/ready authority blocks
implementation; selecting a tasks directory or overriding an FSM locally cannot
clear that blocker.
