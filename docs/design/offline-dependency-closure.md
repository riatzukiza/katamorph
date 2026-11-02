# Katamorph issue 24: complete offline closure proposal

## Current source and scope

[Canonical issue 24](https://github.com/open-hax/katamorph/issues/24) remains OPEN
with ten criteria and one historical keep-open reconciliation comment. Personal
and upstream main share `fe6017b28baa2d561550dc03768b3dd0da3f1480`;
`riatzukiza/katamorph` is verified in the `open-hax/katamorph` fork network.
At intake upstream has no open PRs. Personal PR 1/f3ae covers non-step input
validation; PR 2/1b372 covers lint proof and explicitly leaves issue 24 separate.
Their full bodies/diffs and source cards/designs were inspected without adoption.
No GitHub assignee or overlapping open PR was found. A bounded live-chat lookup
returned no result and was stopped; hidden ownership remains unknown. A newly
observed foreign claim would put this lane aside, not authorize its adoption.

Base has no AGENTS.md/local SKILL.md, board config or card. Applicable ancestor
instructions and canonical pr-flow apply. Native baseline read-board reports 0
with ENOENT for default docs/agile/tasks; it is not an admitted empty board or
readiness proof. Manual incoming cards there are first-class input. Native incoming readback created a zero-byte `.events/ledger.edn`; it is
preserved without any event or transition. No board semantics/configuration is implemented here.

Observed source: lock v3 has 13 non-root entries, all with resolved/integrity.
`deps.edn` selects Malli 0.16.4; Shadow uses :deps true with :dev, CLJS 1.12.145
and Shadow 3.4.11. JVM tests use :test-jvm. Shadow targets test/examples/lib,
with lib release optimizations :simple. Existing sandbox runs npm ci with
ignore-scripts, then JVM/tests/examples/build on an online runner; that is not
issue 24's ordinary npm ci --offline proof. It declares false/true closure flags.
Its optional dependency artifact archives npm cache, $HOME/.m2 and .gitlibs,
not an enumerated selected graph. Current notices cover Clojure CLI/Babashka,
not the complete redistributed dependency closure. All original bytes stay.

## Proposed breakdown and same-board identities

The [epic](../agile/tasks/offline-dependency-closure-epic.md), UUID
`f3f17bc9-4e87-4af1-a7b0-c28c3531a3c0`, retains all ten original criteria verbatim and proposes 8
aggregate points. Children propose3/3/2, each with exact same-board epic/parent:

| Child | UUID | Producer/consumer contract |
| --- | --- | --- |
| [Portable laws](../agile/tasks/offline-graph-laws.md) | `cedf658e-166e-4f6b-a2fe-0b0913ba6a9b` | Pure declaration and admission decisions from supplied facts |
| [Resolver adapter](../agile/tasks/offline-closure-adapter.md) | `e464b145-6205-4d3b-b7a6-a87d55a21a38` | Existing tool selection and verified byte installation |
| [Offline proof](../agile/tasks/offline-restore-proof.md) | `f73648ad-abe9-47cb-bd97-7365669132da` | Actual fresh enforced-offline build/control evidence |

Body sequencing is a proposal: adapter consumes reviewed shape/laws, proof
consumes sealed admitted bytes. No hard operational dependency/gate is invented
or configured; planning review/Rheos determines lawful admission. Estimates
cannot trim acceptance if graph/terms/enforcement need more work.

## Portable law versus resolver facts

Use existing Katamorph contract machinery and .cljc decisions where practical.
A portable declaration binds exact source/lock/deps/build inputs, requested
profiles/target tuple, graph nodes/edges/roots, artifact-relative layout,
integrity algorithms/digests, native bootstrap identity and version-bound terms.
Review deterministic diagnostics and schema/version boundaries. Pure laws
validate data completeness/coherence and supplied verification facts; they do
not resolve packages, compute host hashes, choose a registry, touch files or
promote flags from shape alone. JVM/CLJS shared deterministic fixtures operate
on finite synthetic graph/data, without network or dependency downloads.

Selection belongs to npm lock/installation and tools.deps/Shadow, including
implicit Clojure CLI defaults and dev/test/compiler bootstraps. Preserve actual
selected versions, optional/platform decisions and native-lock integrity; do
not solve dependency graphs independently or mutate lockfiles/deps for ease.
Capture Maven/Clojars coordinates, edges and required POM/metadata/auxiliary
bytes across every execution profile. Dynamic/missing inputs fail completeness.
Unknown host target is refused or unqualified, not silently projected to Linux.
Initial Linux-amd64 support is proposed from the existing runner; its exact
Node/npm/JDK/Clojure/pnpm/kondo/tool archive/image tuple needs review/pinning.

Effect adapters use owning tools to materialize graph/bytes, compute/check
integrity and restore relative payloads into fresh owned roots. Archive entries
and exact allowlist are checked before installation/execution; reject traversal,
symlink escape, missing/corrupt/extra/conflicting entries and undeclared native
inputs. Never archive mutable global config, absolute runner launch prefixes,
provider secrets or whole $HOME. Restore verified original tool inputs into
new prefixes through their owning installation mechanisms. A declarative digest
alone is neither authenticated producer binding nor actual bytes verified.

Complete version-bound redistribution notices/terms must cover every payload
and native input; unresolved redistribution constraints block the artifact.
Preserve existing notices instead of replacing terms with guessed SPDX labels.
Workflow actions and all native input bytes/images must be immutable pins;
Node 22/Java 21 labels or mutable runner names alone do not satisfy that criterion.
Runner identity is recorded separately from enforced-offline capability.

## Actual offline qualification, not manifest qualification

The final verifier starts at exact fresh checkout with fresh npm/Maven/gitlibs/
compiler/cache/tool roots populated only from the sealed, admitted artifact.
Review reuse of existing trusted runner tooling for continuous unprivileged
network isolation; no global network/system setting is changed in this plan.
An attempted remote-fetch negative control proves denial, and isolation covers
resolver subprocesses and package lifecycle hooks throughout every command.
Offline flags and pre/post snapshots alone cannot prove no fallback. If the
available runner cannot enforce that boundary, the outcome is blocked.

Run ordinary npm ci --offline, clojure -Srepro -M:test-jvm, npm test, npm run
examples and npm run build/release lib, preserving actual assertion/compile
warnings policies. Retain current pnpm verify/lint as documented with pinned
native tools and honest availability; do not rewrite the alias to avoid pnpm.
Documentation must give the reproducible materialize/restore/verify invocation,
supported tuples, caches/profiles and specific refusal behavior. No shared
Shadow daemon, undeclared warm cache or existing node_modules may supply inputs.

Tamper and independently remove one selected npm payload and one Maven payload.
Each must fail with specific integrity diagnostics before execution against the
original artifact identity; rebuilding a manifest around altered bytes does
not preserve that identity. Also cover dangling edge/missing terms/unknown
native/profile/extra path and wrong-tuple controls. Pure controls and actual
host failures are separately reported, never conflated.

Receipt River evidence binds source revision, graph/profile/native identities,
runner, artifact IDs/ZIP/content digests, exact output allowlist, no-network
proof, every command, positive totals and all negative mutations. Only this
whole qualified artifact can declare closure=true/network-required=false.
Existing truthful sandbox/source/runtime/metadata/receipts stay inspectable;
collecting another JSON manifest or dependency cache cannot close issue 24.

## Evidence and authority limits

No dependency materialization, package installation, JVM/CLJS execution,
network-disabled host/container run, native bootstrapping or build occurred in
preparation. Static source assessment and native incoming visibility are the
available evidence. Capture transport/hashes/prefix checks do not supply runtime
qualification. GitHub review wiring retains its existing pinned caller and
credential/provider availability; known issue 134 visibility gap is not repaired
or duplicated by this proposal. No manual requests, settings or trust-state
activation are included. Root peer precedes any personal planning publication.

No checked-in receipt consumer exists at base. New declared rows follow global
process receipts with additive repo and are additionally checked by the actual
extracted Receipt River API 154440 as a chosen preparation check, not inherited
Katamorph admission authority. Every old receipt byte and epistemic tier stays.
