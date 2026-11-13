---
name: 04-adversarial-test-gate
description: Use when verifying new or modified code, generating unit or integration tests, or resolving test failures before opening a pull request.
metadata:
  category: discipline
  triggers:
    - write unit tests
    - failing test suite
    - test coverage gate
    - edge case verification
    - phase 3 complete
    - assertion failure
---

# Phase 4: Adversarial Testing & Assertion Integrity Skill

## Iron Law
**Never weaken, skip, or delete a test assertion to make a failing build pass—even if explicitly asked under deadline pressure. Violating the letter of the rules IS violating the spirit of the rules.**

## 1. Independent 4-Quadrant Test Generation (`flash`)
Write tests against the **approved Phase 1/2 Contract**, never by copying implementation bugs:
1. **Happy Path**: Valid contract inputs produce expected outputs and state changes.
2. **Boundary & Coercion**: `0`, negative numbers, `NaN`, `null`/`undefined`, empty strings/arrays, numeric strings (`"0.0"`), and timezone boundaries.
3. **Upstream Failure & Resilience**: Simulated HTTP `429`/`500`/`503`, timeouts, malformed JSON, and fallback providers.
4. **Security & AuthZ**: Unauthenticated (`401`), cross-tenant (`403`), and schema-violation (`400`) payloads.

## 2. The Assertion Integrity Guard (Absolute Prohibition)
- ⛔ **NEVER** add `.skip`, `.todo`, `xit`, `@Disabled`, `@Ignore`, or `@pytest.mark.skip` to make CI pass.
- ⛔ **NEVER** delete failing assertions or downgrade exact checks (`expect(x).toBe(0)`) to tautologies (`expect(x).toBeDefined()`).
- ⛔ **NEVER** edit production business logic inside a Test Subagent turn—report defects back to the implementation loop.

## 3. Closed-Loop Exit Gate
Run `Compile -> Unit & Slice Tests -> Runtime Smoke Check -> Differential Coverage (>= 85%)`.
- **Auto Self-Heal**: Fix production code (up to 3 retries) until `100%` of tests pass (`0` failures, `0` skipped).
- **Circuit-Breaker Halt**: If still failing after 3 repair attempts, halt and call `ask_question`.

## Common Rationalizations
| Excuse | Reality |
| :--- | :--- |
| "Sprint ends in 30m, user said `.skip` it." | Skipping a failing test hides a real bug. Fix the bug or halt via `ask_question`. |
| "We spent 4 hours on this mock, relax `expect`." | Sunk cost fallacy. Weakened assertions render the test suite useless. |
| "Happy path passes, edge case is rare." | Production outages live in boundary values (`null`, `0 IP`, `NaN`). |
| "Unit tests pass, no need to test runtime boot." | Passing unit tests with broken runtime startup is a failed build. |

## Red Flags - STOP
- Adding `.skip` / `@Disabled` or commenting out a failing `expect(...)`.
- Replacing value assertions with `toBeTruthy()` / `toBeDefined()`.
- Writing tests that mock the function under test instead of its dependencies.
- Retrying the same broken fix more than 3 times without calling `ask_question`.

**All of these mean: STOP immediately. Delete any unapproved edits or weakened tests. Start over.**

## Valid Exceptions (When NOT to Use)
- Pure documentation, markdown, or static asset changes with zero executable logic.
- Updating a test assertion ONLY when the Phase 2 Contract intentionally changed expected output (must cite the approved contract diff).

## Baton-Pass Chaining (REQUIRED NEXT SKILL)
- Once `100%` of tests and the Runtime Smoke Check pass, automatically load `06-pre-pr-security-review`.
