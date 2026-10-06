---
uuid: ab741ef4-8310-4853-a59e-79dbc042c038
title: Prove current Katamorph Malli lint policy without hiding errors
status: incoming
priority: P2
points: 3
labels: planning, lint, malli, issue-23
---

# Context

[Canonical issue 23](https://github.com/open-hax/katamorph/issues/23) preserves
seven criteria from superseded PR 4. This story retains that complete outcome;
it does not revive the stale branch. Upstream and personal main share
`fe6017b28baa2d561550dc03768b3dd0da3f1480`. No checked-in board configuration or cards exist
at that baseline. Incoming Markdown is planning input, not reviewed admission
or a lawful ready transition.

Current lint config contains the modern-CLJS `await` exclusion; `.gitignore`
lacks `.lsp/`. Focused five-root lint passes with installed 2025.07.28 and
privately checksum-verified CI-pinned 2025.10.23. Source has no
`malli.experimental/defn` or `schema.core/defn` use. These observations neither
prove Malli schema/type linting nor establish a current-source false positive.
See the [design and evidence contract](../../design/malli-lint-proof.md).

# Outcome

A fresh standalone checkout has a documented, version-bound, repository-local
Malli lint policy with demonstrated loading and useful CLJ/CLJS behavior;
real errors remain detectable and generated editor state remains untracked.
Runtime contract validation and portable source semantics retain their authority.

# Scope

Reproduce and classify current-source findings first; then review meaningful
CLJ/CLJS fixtures, a supported pinned policy, integration/loading proof and
`.lsp/` ignore. Proposed three points cover this bounded verification outcome.
A new general schema analyzer or unavailable DSL feature requires separate
reviewed breakdown instead of expanding this story.

# Non-goals

No runtime/source semantics change, blanket suppression, gratuitous macro
migration, broad `lint-as`, stale PR 4 merge, board engine/config override, or
dependency/provider/environment policy change. Personal PR 1/issue 25 input
validation and issue 24 offline dependency closure remain separate and open.

# Acceptance criteria

All seven original issue 23 criteria remain required:

- [ ] Capture actual current-main CLJ and CLJS false positives as focused fixtures
      before changing lint policy. Record when none is found; a hypothetical DSL
      example is not existing source. Reconcile that criterion through review
      with the canonical issue owner before claiming completion or implementing
      an unnecessary mapping.
- [ ] Enable Malli lint semantics through supported repository-local clj-kondo
      config or hooks and prove loading. Identify pinned versions, applicable
      forms, exact root/import/config paths and supported mechanisms. Document
      invocation in developer guidance and integrate the existing lint entry
      point without weakening it.
- [ ] Preserve modern-CLJS `await`: valid async fixtures pass while unrelated
      unresolved symbols still fail.
- [ ] Malformed schema definitions still fail lint. Meaningful CLJ/CLJS controls
      separate valid supported declarations, malformed ones and deliberate real
      arity/type errors. Prevent a broad false-negative zone. Where schema shape
      validity requires runtime evaluation, identify the limitation and obtain a
      reviewed supported solution; ordinary kondo must not be claimed to validate
      arbitrary schema vectors.
- [ ] Ignore generated editor state generically with `.lsp/`, covering existing
      and future cache names while outside source/config stays tracked/lintable.
- [ ] From a fresh checkout run clj-kondo over `src/cljc`, `src/cljs`, `test/cljc`,
      `test/clj`, and `test/cljs` with zero unexpected findings. Preserve existing
      `examples/cljs` coverage in the lint entry point too.
- [ ] Record exact revision, command, fixture results, loaded config paths and
      positive/negative controls in Receipt River. Report refusals and distinguish
      baseline observations from red/green proof.

# Verification

Native planning convergence and a lawful Rheos ready operation must precede
implementation. Baseline native read-board returned 0 tasks plus ENOENT for its
default directory: no existing ready-card proof. RED must reproduce meaningful
failures; GREEN uses the same pinned linter, private caches and fixture inputs.
A loading control removes only the selected config/import in a disposable copy
and must change the targeted result. `await` and unrelated real-error controls
remain separate. Verify both language modes, schema/arity/type failures,
`.lsp/`/outside ignore controls, five roots and example coverage. Existing
JVM/CLJS suites protect semantics; neither suite was run by this planning work.

# Risks

Malli's generated function-schema lint config does not prove every data schema
is statically checked. A hypothetical macro fixture must not manufacture a
current-source defect. Unsupported diagnostics block qualification pending
review instead of authorizing suppression or a second schema language. The
three-point estimate must be reviewed if a new analyzer would be necessary.
