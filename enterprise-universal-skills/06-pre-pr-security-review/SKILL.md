---
name: 06-pre-pr-security-review
description: Use when implementation and tests are complete, before committing, pushing, or opening a pull request for human review.
metadata:
  category: discipline
  triggers:
    - pre-pr review
    - security audit
    - git commit or pr
    - phase 4 complete
    - dependency check
    - codeowners readiness
---

# Phase 5 & 6: Pre-PR Code & Security Review Skill (`pro` Model)

## Iron Law
**Never mark a task complete or request human PR review while any `[BLOCKER]` finding, unapproved dependency, or failing deterministic gate remains unresolved. Violating the letter of the rules IS violating the spirit of the rules.**

## 1. PR Size & Hygiene Gate
- **Diff Size Cap**: Production code diff (excluding tests and lockfiles) should be **`<= 400 LOC`**. If larger, recommend splitting into stacked PRs.
- **Zero Unresolved Gates**: Confirm `Compile`, `Lint`, `Unit Tests`, and `Runtime Smoke Check` in `AGENTS.md` all exited with code `0`.

## 2. Security & Supply-Chain Audit (`OWASP Top 10 + LLM Top 10`)
- **Secrets & PII**: Verify zero hardcoded credentials, tokens, private keys, or PII fields (`ssn`, `cardNumber`, `password`) in code, tests, or log statements.
- **Injection & AuthZ**: Verify parameterized SQL queries, validated request schemas, and explicit tenant/user ownership checks (`403` on cross-tenant IDs).
- **Anti-Slopsquatting Check**: If `package.json`, `pom.xml`, `build.gradle`, or `requirements.txt` changed, verify every added package is explicitly approved via `ask_question`, `>= 30 days` old in the public registry, and has valid provenance signatures.

## 3. Architecture & Concurrency Audit
- **Transaction Boundaries**: Flag DB transactions (`@Transactional` / `BEGIN`) held open across external HTTP/gRPC calls or message broker publishes.
- **Query Scalability**: Flag unbounded `SELECT *` queries missing pagination or `N+1` loop queries.
- **Resource & Thread Safety**: Flag blocking I/O on event loops / virtual-thread `synchronized` pinning, unclosed streams, or missing HTTP timeouts (`<= 3000ms`).

## 4. Structured Review Output Format & Self-Heal Loop
Output a concise PR triage table categorized as:
- 🔴 **`[BLOCKER]`** — Must be fixed before human review is requested (security flaw, broken contract, missing test, transaction bug).
- 🟡 **`[WARNING]`** — Architectural trade-off or edge case for the human `CODEOWNERS` reviewer to inspect.
- 🟢 **`[VERIFIED]`** — Summary of passing deterministic gates and coverage delta.

## 5. Common Rationalizations
| Excuse | Reality |
| :--- | :--- |
| "It's an internal tool, skip OWASP check." | Internal tools share DBs and credentials; lateral movement starts there. |
| "I needed this helper npm package quickly." | `~19.7%` of LLM-suggested packages are slopsquatted malware traps. |
| "HTTP call inside `@Transactional` is fast." | One slow upstream API call exhausts the entire DB connection pool. |
| "PR is 900 LOC, reviewer can skim it." | Human defect detection drops by `>60%` above `400 LOC`. |

## 6. Red Flags - STOP
- Recommending PR merge while a `🔴 [BLOCKER]` or failing test exists.
- Adding a dependency to `package.json`/`pom.xml` without prior `ask_question` approval.
- String-interpolating variables into SQL queries or shell commands.
- Leaving `console.log` / debug print statements containing tokens or user payloads.

**All of these mean: STOP immediately. Delete any unapproved edits or weakened tests. Start over.**

## 7. Valid Exceptions (When NOT to Use)
- Read-only research or architectural Q&A turns with zero file diffs.

## 8. Baton-Pass Chaining (REQUIRED NEXT SKILL)
- **If `🔴 [BLOCKER]` found**: Automatically loop back to repair code and re-run `04-adversarial-test-gate`.
- **If `0` Blockers (`🟢 [VERIFIED]`)**: Present the final Pre-PR Verification Report and change notes to the developer.
