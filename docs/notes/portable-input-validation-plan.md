# Portable input validation planning evidence

This proposal addresses [open-hax/katamorph issue 25](https://github.com/open-hax/katamorph/issues/25).
It adds planning artifacts only. The [incoming card](../agile/tasks/b2e7a944-8ab6-4e34-ada2-102763af58fc-portable-input-validation.md)
owns implementation acceptance after reviewed ready admission.

## Exact baseline and routing

Observed on 2026-10-06: `riatzukiza/katamorph` is a fork whose GitHub parent and
network source are `open-hax/katamorph`; both default branches are main. Both main
refs resolve to `fe6017b28baa2d561550dc03768b3dd0da3f1480`, with no divergence.
The accepted personal development map is preserved in Foresight personal sync
PR 3 at `96a6dca24cb7a14b041bdd6e3e7922c568238da9`,
`config/dev-origins.edn`. The row maps `katamorph` to those repositories.
Development publication targets the personal fork. Final origin release remains
separately qualified work. Personal auto-merge is disabled; this plan changes
no repository settings, workflows, controllers or secrets.

The independent bare clone and worktree are under
`/home/err/.codex/parallel-goal/child-prs-20261006/katamorph-issue25-plan.git`
and `katamorph-issue25-plan-pr`. No existing checkout or shared cache was modified.

## Current source assessment

- `src/cljc/katamorph/action/input.cljc` owns the reserved heads and all reference
  shapes. Its `PortableLiteral` excludes reserved-head vectors, while `LiteralRef`
  provides explicit escaping. `schema/step.cljc` uses that `InputValue` language.
- `workflow/wire.cljc` checks consumer identity and declared input first, then
  returns `:workflow/unsupported-reference` for every value other than a three-item
  `:step` vector. It does not classify lawful non-step sources or check literals.
- Step references retain unknown producer, undeclared output and incompatible
  contract diagnostics. `workflow/compatibility.cljc` uses
  `schema/relation.cljc`, which proves only supported subtype relations and fails
  closed for unproved relations. Identical schemas are compatible only after
  well-formedness checks; nominal well-formedness alone is not resolution.
- `workflow/graph.cljc` reads only step references as dependency edges. Existing
  workflow tests assert unsupported literal and malformed-reference findings;
  shape tests assert acceptance of the generalized language. Those are the
  documented boundary, not proof that issue 25 is already implemented.
- No AGENTS.md, local SKILL.md, board configuration or prior card was present in
  the baseline Git tree. Global pr-flow and its planning skill apply. No board
  projection, validation or transition was attempted.

A read-only attempt to require the actual wire namespace through installed NBB
with `-cp src/cljc` failed before executing probe calls because `malli.core` was
unavailable. No dependency was installed and no namespace was mocked. The gap
above is source inspection evidence, not an executed semantic reproduction or a
fresh JVM/CLJS test result. The issue's historical PR 17 hosted proof remains
historical evidence, not evidence for this new proposal.

## Proposed semantic decisions for planning review

The optional source-contract map is exact-reference keyed, so overlapping event
paths cannot silently inherit parent schemas and resource identifiers cannot
select providers. Its schemas are explicit assumptions passed by the caller;
validation must not dereference them against a host registry. Each of the four
non-step reference kinds dispatches through its existing shape and then the same
provided <: required law as typed step ports. Absence is a missing-source-contract
finding, incompatibility or an unsupported relation is an unproven-source-contract
finding. These provisional names and precedence need native planning review.

Literal proof uses the actual known value, rather than treating every literal as
an opaque source. Proposed supported schemas are pure built-in scalar contracts,
`:any`, `:=`, `:enum`, `:or`, `:and`, and portable map/map-of/vector/tuple/set
compositions using reviewed optional/closed/size properties. Only forms whose
literal semantics are portable and proved by shared fixtures are admitted.
Registered nominal schemas require an explicit portable declaration; unresolved
names, unsupported properties, arbitrary `:fn` predicates and host schemas yield
an unproven-literal-contract diagnostic. Reuse existing contract machinery after
that boundary check; do not create a competing schema engine. The repository's
trusted portable-value schema retains its existing numeric boundary.

The literal wrapper unwraps once: `[:literal [:step :x :y]]` is data, not a second
reference evaluation. Nil and false must be validated as values, not treated as
missing inputs. Malformed reserved vectors fail before literal dispatch. Existing
consumer/input checks retain precedence; source-kind diagnostics follow shape
checks. Define deterministic error ordering for workflow-level aggregation in
shared fixtures rather than relying on map iteration order.

Review must confirm this finite supported schema subset, the additive API and
estimate before ready admission. If proof requires substantial new schema laws,
split that work into a linked reviewed card; do not downgrade every non-step kind
to blanket unsupported or silently execute a host predicate.

## Evidence and admission limits

The existing receipt ledger baseline SHA-256 is
`2145939103b49d5e55b99cb34aef7db6eac5cb8b629e16cd8d0d91bae5206651`.
Preserve every baseline byte; append only new owned receipts. Historical records
retain their original shapes and epistemic tier.

Five points is a proposal for the pure validation and shared-fixture slice,
subject to planning review. This artifact is incoming. Native planning approval,
required checks and an actual Rheos ready event are absent at preparation time.
The personal-account manual CodeRabbit cooldown is held until
`2026-10-06T15:01:50Z`; afterward a fresh deduplication and quota check is required.
Optional unavailable reviewers never become approvals. No implementation starts
from this card until the lawful readiness blocker is cleared.
