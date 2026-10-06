---
uuid: f73648ad-abe9-47cb-bd97-7365669132da
title: Prove fresh offline restoration and integrity refusals at exact revision
status: incoming
priority: P2
points: 2
epic: f3f17bc9-4e87-4af1-a7b0-c28c3531a3c0
parent: f3f17bc9-4e87-4af1-a7b0-c28c3531a3c0
labels: planning, offline, dependency-closure, issue-24
---

# Context

Successful deterministic graph fixtures and collected caches are insufficient.
The sealed candidate must actually rebuild/test in a fresh environment while
network access is enforced off, with controlled corrupted npm/Maven payloads.

Owning [epic](offline-dependency-closure-epic.md) retains all ten canonical
issue 24 criteria. This child alone cannot close the epic or assert offline
closure; proposed sequencing and consumer contracts are in the
[design](../../design/offline-dependency-closure.md).

# Outcome

Run every original command, retained verify/lint coverage and negative integrity
controls from the sealed artifact in a supported explicit runner tuple. Bind
source/tool/graph/artifact identity and actual no-network enforcement evidence;
truthful flags follow that whole outcome.

# Scope

This is the proposed 2-point proof slice of the complete outcome. Review
its estimate, platform/tool/profile coverage and producer/consumer boundaries
before implementation; additional capabilities require reviewed breakdown.

# Non-goals

No online fallback, npm script bypass, warmed undeclared cache, fake skipped
pass, disabled real checks, production deployment or system-wide network change.
No candidate code executes with publisher/deployment credentials.

# Acceptance criteria

- [ ] Review an existing supported unprivileged network-isolation mechanism and
      prove networking is off throughout restore/build, including package hooks
      and resolver subprocesses. An invalid remote fetch control must fail;
      offline CLI flags or before/after connectivity checks alone do not suffice.
      If enforcement is unavailable, qualification stays blocked.
- [ ] Fresh exact checkout and fresh private npm/Maven/gitlibs/compiler/tool roots
      contain only sealed admitted inputs; inspect user/global config influence
      without copying or changing shared configuration. No Shadow daemon reuse.
- [ ] With networking off, actually pass npm ci --offline, clojure -Srepro
      -M:test-jvm, npm test, npm run examples and npm run build (optimized lib).
      Also pass current pnpm verify/lint coverage with its pinned native tools;
      preserve existing warning/error policy and document all invocation paths.
- [ ] Independently mutate AND remove a selected npm payload, then a selected
      Maven payload: each restore must fail with a specific integrity diagnostic
      before execution. A control digest manifest rebuilt around bad bytes must
      not replace the admitted artifact binding.
- [ ] Record source, runner/tool versions/digests, graph identity, artifact IDs/
      digests, networking proof, command logs/test totals and all red/green
      controls in append-only receipts. All admitted payloads have complete terms.
- [ ] Set closure=true/network-required=false only in the separately named artifact
      that passes every epic criterion. Missing/skipped commands, notices,
      corrupted input or unavailable enforcement preserve failure/unqualified
      state; retain original artifact/history and reviewed restore documentation.

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
