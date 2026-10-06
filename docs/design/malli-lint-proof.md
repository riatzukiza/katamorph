# Issue23: evidence before choosing Malli lint policy

## Source and ownership

Planning proposal for [canonical issue 23](https://github.com/open-hax/katamorph/issues/23)
and [incoming story ab741ef4-8310-4853-a59e-79dbc042c038](../agile/tasks/malli-lint-proof.md).
Baseline `fe6017b28baa2d561550dc03768b3dd0da3f1480` is current upstream and personal main.
The verified personal network is `riatzukiza/katamorph` -> `open-hax/katamorph`.
At intake upstream has no open PRs; personal PR 1 alone covers issue 25 at
`f3ae16f514674e5171bc5f89e971d3674128598c`. Full fork branch/tag history is
retained in the independent private clone. Original PRs, policies, source,
events and receipt prefixes remain unchanged.

There is no checked-in AGENTS.md, local SKILL.md, board config or card at base.
Ancestor instructions and canonical pr-flow control planning/qualification.
`docs/agile/tasks` is the installed reader's default path; choosing incoming
Markdown there grants neither board activation nor readiness. No operational
transition is included.

## Current observations

1. `.clj-kondo/config.edn` has only unresolved-symbol exclusion `[await]`, with
   no loaded Malli import/hook/type policy.
2. `deps.edn` selects Malli 0.16.4. Source uses ordinary definitions/data schemas,
   not `malli.experimental/defn` or `schema.core/defn`. `katamorph.schema.core` is
   this project's portable registry API, not a Schema-library macro mapping.
3. Installed 2025.07.28 and CI-pinned 2025.10.23 each report 0 errors / 0 warnings on
   the five-root command. The latter ZIP was privately downloaded and verified
   against its published SHA-256. No global tool/config/cache changed. This is
   baseline lint, not fixture red/green proof or hosted CI evidence.
4. `git check-ignore .lsp/.cache/db.transit.json .lsp/new.generated` returns 1;
   neither name is ignored. No files were created at those paths.
5. Native `kanban read-board` returns exit 0 / total 0 plus ENOENT for absent default
   `docs/agile/tasks`. This bounded output is not board validation, ready proof
   or authority for alternate semantics.

Exact commands and lossless captures are in
[the evidence index](../../.ημ/verification/katamorph23-planning/README.md).
No current false positive was reproduced; no Malli lint fixture or runtime
schema validation was run. All seven issue criteria remain unclosed.

## Supported-policy decision to review

Malli 0.16.4's [documented integration](https://github.com/metosin/malli/blob/0.16.4/README.md#clj-kondo)
collects function schemas and emits `.clj-kondo/configs/malli/config.edn`.
Its example maps `malli.schema/defn` to `schema.core/defn` and emits arity/type
information. This is a supported function-schema mechanism, not proof that
PR 4's different `malli.experimental/defn` mapping applies to current source.

Identify which existing declarations require which semantics before choosing
policy. Generate/select supported pinned policy in a disposable preparation
environment; load it explicitly through the local entry point. An import file
alone is not loading proof. Bind generation to owning namespace/version and
retain provenance; global caches/sibling source paths are not authority.

For each supported CLJ/CLJS form propose valid positive, targeted arity/type
failure, malformed-schema failure and unrelated-error controls. Removing only
the selected config/import in a private copy must change targeted diagnostics,
with language mode and other root policy held fixed. If current-source false
positives cannot be produced, obtain reviewed canonical-issue disposition
before claiming the original criterion satisfied. Prospective DSL fixtures
must remain explicitly prospective.

Static lint and runtime schema validity are separate facts. If the supported
tool cannot diagnose a malformed data schema, retain that limitation as a
blocker and review integration/breakdown. Do not invent a blanket vector
validator, suppress all unresolved symbols or add a general schema engine to
obtain green counts.

## Red/green and complete outcome

After planning qualification/native readiness, RED proves the actual defect
or explicitly reviewed prospective contract and meaningful failing controls.
GREEN selects narrow supported policy, documents paths/invocation, integrates
existing lint, retains `await`, ignores all `.lsp/` state and meets every issue
criterion. Config-disabled, unrelated-symbol, malformed-schema and wrong
arity/type controls still fail for their expected reason. Disabling a linter
is not a repair.

Fresh-checkout evidence binds CI-pinned 2025.10.23, private caches, all five
requested roots, existing example coverage, revision, fixture outcomes and
loaded paths in Receipt River. Existing JVM/CLJS suites retain regression
coverage; neither suite nor hosted CI ran for this planning candidate.

## Independent issue 24 boundary

Issue 24 still requires complete npm/Maven graphs, immutable native inputs,
redistribution notices and integrity-checked fresh offline restoration passing
all named suites/builds, including corrupt/missing npm and Maven controls.
Current sandbox manifest still says closure=false/network-required=true; its
optional artifacts only archive runner caches. This story changes no workflow
and supplies no offline artifact. Issue 24's full ten criteria remain open.

## Publication and authority limits

The checked-in caller grants read-only contents/PR permissions and invokes
`open-hax/eta-mu/.github/workflows/opencode-code-review.yml` at immutable
`2b918cdab2ebd30e745bb8fa86d077d7a3af0030`. Inspection found no merge step in
that pinned reusable workflow. Credential/provider availability, native input
coverage and checks require readback at the actual published head; wiring is
not approval or activation proof. Auto-merge stays off; reviewed planning and
lawful readiness block implementation. Root coordinates manual invitations.
No review request, settings/secret change or workflow change is included.
