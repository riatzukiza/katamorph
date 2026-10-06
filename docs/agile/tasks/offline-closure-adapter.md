---
uuid: e464b145-6205-4d3b-b7a6-a87d55a21a38
title: Materialize and restore exact dependency bytes through owning resolvers
status: incoming
priority: P2
points: 3
epic: f3f17bc9-4e87-4af1-a7b0-c28c3531a3c0
parent: f3f17bc9-4e87-4af1-a7b0-c28c3531a3c0
labels: planning, offline, dependency-closure, issue-24
---

# Context

Current npm lock v3 declares thirteen non-root records with resolved locations
and integrity values; Maven selection is owned by tools.deps/Shadow for the
actual JVM/dev/build profiles. Optional host-cache tarballs are not that proof.

Owning [epic](offline-dependency-closure-epic.md) retains all ten canonical
issue 24 criteria. This child alone cannot close the epic or assert offline
closure; proposed sequencing and consumer contracts are in the
[design](../../design/offline-dependency-closure.md).

# Outcome

Collect actual selected npm/Maven/Clojars graphs with existing owning tools,
materialize every required byte and complete notices, verify digests, and restore
a separately named sealed closure artifact into owned fresh relative cache and
tool prefixes. Keep resolver/filesystem/process effects outside portable laws.

# Scope

This is the proposed 3-point adapter slice of the complete outcome. Review
its estimate, platform/tool/profile coverage and producer/consumer boundaries
before implementation; additional capabilities require reviewed breakdown.

# Non-goals

No new resolver, alternate dependency policy, blanket $HOME or mutable global
configuration archive, in-place shared cache changes, runtime daemon attachment,
credential inclusion or success claim from pre-existing caches.

# Acceptance criteria

- [ ] Selected npm graph binds package-lock.json, actual target selection and
      every tarball/integrity value. Maven/Clojars graph binds deps.edn, toolchain
      defaults and Shadow :dev plus :test-jvm/test/examples/lib profiles, including
      transitive coordinates, POM/metadata/auxiliary inputs actually required and
      SHA-256 values. Unknown resolver inputs block completeness.
- [ ] Native tools/bootstraps/actions are version/digest-bound; include or prove
      offline availability of Node/npm, Java, Clojure, Shadow, pnpm and clj-kondo
      needed by all retained commands. No floating version selector is a pin.
- [ ] Complete version-bound third-party notices/terms cover redistributed npm,
      JVM and native payloads; existing two toolchain notices alone do not suffice.
- [ ] Artifact allowlist, source/graph binding and exact-byte integrity are checked
      before installation/execution, with a fresh confined destination. Reject
      missing/corrupt payload, unexpected path/entry and conflicting identity;
      no runner-absolute prefixes, embedded credential/config or hidden cache use.
- [ ] Restore translates relative declarations into fresh owned cache/tool roots
      without changing dependency selection or archiving mutable global config.
      Design/test permission, symlink/traversal and no-network failure boundaries.
- [ ] Document materialization/restore invocation and refusal modes, retain the
      original truthful sandbox artifact, and never claim this producer is an
      offline execution proof before the separate proof story succeeds.

# Verification

No implementation starts before qualified native planning review and lawful
Rheos readiness for the owning story. Incoming Markdown is first-class input,
not a status transition, board activation or readiness proof. No source,
workflow, dependency policy, settings, secret or shared runtime changes occur
in this candidate. Native readback demonstrates input visibility only.

Bind meaningful red/green controls to one exact implementation revision and
retain all original issue/epic criteria. No fixture, graph materialization,
restore or offline build is claimed executed by this planning candidate.

# Risks

Implicit native inputs and unsupported tools must remain visible. No single
manifest, static test, cache archive or provider verdict supplies the complete
runtime proof. Do not lower acceptance to fit the proposed estimate.
