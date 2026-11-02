---
uuid: cedf658e-166e-4f6b-a2fe-0b0913ba6a9b
title: Specify portable closure graph and artifact admission laws
status: incoming
priority: P2
points: 3
epic: f3f17bc9-4e87-4af1-a7b0-c28c3531a3c0
parent: f3f17bc9-4e87-4af1-a7b0-c28c3531a3c0
labels: planning, offline, dependency-closure, issue-24
---

# Context

The selected graph and payload declaration are portable data; resolution,
network access, digest computation and filesystem installation are outer facts.
Use Katamorph's existing pure contract machinery and .cljc laws, not a second
resolver or schema engine.

Owning [epic](offline-dependency-closure-epic.md) retains all ten canonical
issue 24 criteria. This child alone cannot close the epic or assert offline
closure; proposed sequencing and consumer contracts are in the
[design](../../design/offline-dependency-closure.md).

# Outcome

Prove normalized graph identity, edges, requested execution profiles/target tuple,
relative payload layout, complete content/digest and notice references, and
fail-closed admission from supplied verification facts. Preserve npm integrity
values and Maven coordinates/checksums instead of inventing selection.

# Scope

This is the proposed 3-point law slice of the complete outcome. Review
its estimate, platform/tool/profile coverage and producer/consumer boundaries
before implementation; additional capabilities require reviewed breakdown.

# Non-goals

No package downloads, hash I/O inward, transitive-resolution reimplementation,
provider trust/signing machinery or actual offline-success claim.

# Acceptance criteria

- [ ] Reviewed shape carries exact source and lock/deps/build identities, npm and
      Maven roots/edges, immutable payload/native digest references, target tuple,
      supported command profiles, complete version-bound notices and tool inputs.
- [ ] Every resolved root/edge has a declared payload or explicit reviewed
      toolchain input; missing/conflicting/extra/unknown entries fail closed.
      npm integrity strings remain algorithm-qualified; verified byte digests
      are facts from adapters, never invented by the pure layer.
- [ ] Portable relative paths reject traversal/absolute destinations; canonical
      ordering yields repeatable declarations without rewriting native locks.
- [ ] Positive and one-at-a-time negative no-network fixtures cover missing npm
      and Maven entries, bad digest facts, dangling/unknown edges, duplicate
      identity conflicts, incompatible tuple, absent notices and incomplete
      build/profile coverage. Independently test traversal, POSIX absolute
      destinations, Windows drive paths and UNC paths as separate negative
      cases. Run these shared .cljc fixtures under both JVM and CLJS.
- [ ] Admission cannot turn artifact flags true from manifest shape alone: actual
      verification facts for every required command/control remain mandatory.
      Document version/schema and deterministic diagnostics for adapter consumers.

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
