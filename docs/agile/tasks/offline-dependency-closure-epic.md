---
uuid: f3f17bc9-4e87-4af1-a7b0-c28c3531a3c0
title: Epic - prove complete network-disabled Katamorph dependency restoration
status: incoming
priority: P2
points: 8
labels: epic, planning, offline, dependency-closure, issue-24
---

# Context

[Canonical issue 24](https://github.com/open-hax/katamorph/issues/24) retains the
full offline artifact follow-up to PR 19. Baseline
`fe6017b28baa2d561550dc03768b3dd0da3f1480` still emits truthful
closure=false/network-required=true flags and optional runner-cache archives.
Neither is a dependency-complete, network-disabled execution proof.

# Outcome

A separately named dependency-closure artifact and restore verifier rebuild and
test one exact Katamorph revision in a fresh checkout/fresh caches with network
access enforced off. Selected npm and Maven/Clojars graphs, native inputs,
version-bound redistribution terms, positive builds and adversarial failures
are all revision-bound evidence. The existing honest sandbox stays inspectable.

# Scope

Proposed aggregate eight points, split into portable graph/shape laws (3),
existing-resolver materialization/restore adapters (3) and real offline build
proof/controls (2). Estimate and breakdown need review; unavailable resolver or
network-isolation capability blocks qualification rather than reducing scope.
See [the design](../../design/offline-dependency-closure.md) and children:
[graph laws](offline-graph-laws.md), [adapter](offline-closure-adapter.md),
[offline proof](offline-restore-proof.md).

# Non-goals

No JSON-manifest-only closure claim, cache snapshot as authority, new package
resolver or runtime, dependency-policy rewrite, replacement schema/board engine,
foreign worker/source adoption, shared cache/server use, secret distribution or
trust-state activation. PR 1/issue 25 and PR 2/issue 23 remain independent.

# Acceptance criteria

All ten original issue 24 criteria remain required, verbatim:

- [ ] Materialize the exact npm graph selected by `package-lock.json`, with every tarball/integrity value retained.
- [ ] Materialize the exact Maven/Clojars graph selected by `deps.edn` and Shadow CLJS, including artifact coordinates and SHA-256 values.
- [ ] Avoid archiving host-specific absolute paths or mutable global configuration as the restore contract.
- [ ] Ship complete version-bound third-party notices and terms for redistributed dependency payloads.
- [ ] Restore into a fresh checkout and fresh cache roots.
- [ ] With network disabled, pass `npm ci --offline`, `clojure -Srepro -M:test-jvm`, the CLJS suite, examples, and optimized library build.
- [ ] Mutate or remove one npm artifact and one Maven artifact; each restore must fail with a specific integrity error.
- [ ] Set `runtime_dependency_closure_included: true` and `network_required_after_restore: false` only in the artifact that passes those laws.
- [ ] Pin every workflow action and native input by immutable digest.
- [ ] Record exact revision, runner, artifact IDs/digests, positive laws, and negative mutations in Receipt River.

Complete outcome also retains the current developer `pnpm verify` entry point,
its lint/example/build coverage and documented restore instructions; an alias
cannot be silently rewritten to avoid a missing native tool. Platform/tool
support is explicit and review-bound, with other tuples refused or unqualified.

# Verification

No implementation starts before qualified native planning review and lawful
Rheos readiness for the owning story. Incoming Markdown is first-class input,
not a status transition, board activation or readiness proof. No source,
workflow, dependency policy, settings, secret or shared runtime changes occur
in this candidate. Native readback demonstrates input visibility only.

Pure deterministic fixtures prove graph/shape/admission laws without a network
or resolver. They cannot prove dependency closure. Adapter evidence must collect
the owning tools' actual selected graphs and bytes; final proof restores only
that sealed artifact, enforces networking off and executes every listed command
plus current verify/lint. Evidence binds source, graph, native inputs, runner,
artifact IDs/digests, all command outcomes and negative mutations. Only the
fully qualified artifact may acquire closure=true/network-required=false.

# Risks

Toolchain implicit dependencies, dynamic Maven metadata, platform-specific npm
selection and lifecycle hooks may add unenumerated inputs. Incomplete notices,
mutable native input, hidden caches/config or unenforced networking are blockers.
No offline build, restored runtime or qualification was executed by this plan.
