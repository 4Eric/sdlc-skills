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

# Phase 5 & 6: Pre-PR Code & Security Review Skill

## Iron Law
**Never mark a task complete or request human PR review while any `[BLOCKER]` finding, unapproved dependency, or failing deterministic gate remains unresolved. Violating the letter of the rules IS violating the spirit of the rules.**

## 1. PR Hygiene & Batch-Size Gate
- **Diff Size Cap**: Production diff (excluding tests/lockfiles) should be **`<= 400 LOC`**. If larger, split into stacked PRs.
- **Deterministic Green Check**: Confirm `Compile`, `Lint`, `Unit Tests`, and `Runtime Smoke Check` from `AGENTS.md` all exited with code `0`.

## 2. Security & Supply-Chain Audit (`OWASP Top 10 + LLM Top 10`)
- **Secrets & PII**: Zero hardcoded keys, tokens, or PII (`ssn`, `cardNumber`, `password`) in code, tests, or logs.
- **Injection & AuthZ**: Parameterized SQL only, validated input schemas, and explicit tenant/user ownership checks (`403`).
- **Anti-Slopsquatting**: Verify `package.json` / `pom.xml` / `requirements.txt` contains zero unapproved or hallucinated packages.

## 3. Architecture & Concurrency Audit
- **Transaction Boundaries**: Never hold `@Transactional` / DB transactions open across external HTTP/gRPC or LLM API calls.
- **Scalability & Timeouts**: Flag `N+1` queries, unbounded `SELECT *`, virtual-thread `synchronized` pinning, or HTTP calls missing timeouts (`<= 3000ms`).

## 4. Mandatory Triage Output & Self-Heal Loop
Output a structured review table (`🔴 [BLOCKER]`, `🟡 [WARNING]`, `🟢 [VERIFIED]`).
- If any `🔴 [BLOCKER]` is found, automatically loop back to fix it and re-run `04-adversarial-test-gate` before presenting the final PR summary.

## Common Rationalizations
| Excuse | Reality |
| :--- | :--- |
| "It's an internal tool, skip OWASP check." | Internal tools share DBs and credentials; lateral movement starts there. |
| "I needed this helper npm package quickly." | `~19.7%` of LLM-suggested packages are slopsquatted malware traps. |
| "HTTP call inside `@Transactional` is fast." | One slow upstream API call exhausts the entire DB connection pool. |
| "PR is 900 LOC, reviewer can skim it." | Human defect detection drops by `>60%` above `400 LOC`. |

## Red Flags - STOP
- Recommending PR merge while a `🔴 [BLOCKER]` or failing test exists.
- Adding a dependency to `package.json`/`pom.xml` without prior `ask_question` approval.
- String-interpolating variables into SQL queries or shell commands.
- Leaving `console.log` / debug print statements containing tokens or user payloads.

**All of these mean: STOP immediately. Delete any unapproved edits or weakened tests. Start over.**

## Valid Exceptions (When NOT to Use)
- Read-only research or architectural Q&A turns with zero file diffs.

## Baton-Pass Chaining (REQUIRED NEXT SKILL)
- **If `🔴 [BLOCKER]` found**: Loop back to repair code and re-run `04-adversarial-test-gate`.
- **If `0` Blockers (`🟢 [VERIFIED]`)**: Present the final Pre-PR Verification Report and change notes to the developer.
