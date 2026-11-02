---
name: 06-pre-pr-security-review
description: Universal Phase 5 & 6 pre-PR audit skill. Screens diffs for OWASP Top 10, supply-chain slopsquatting, concurrency bugs, and PR size limits before human review.
last_verified: 2026-10-30
---

# Phase 5 & 6: Pre-PR Code & Security Review Skill (`pro` Model)

Run this skill on `git diff main...HEAD` before requesting human `CODEOWNERS` review.

## 1. PR Size & Hygiene Gate
- **Diff Size Cap**: Production code diff (excluding tests and lockfiles) should be **`<= 400 LOC`**. If larger, recommend splitting into stacked PRs.
- **Zero Unresolved Gates**: Confirm compiler, linter, and unit tests in `AGENTS.md` exited with code `0`.

## 2. Security & Supply-Chain Audit (`OWASP Top 10 + LLM Top 10`)
- **Secrets & PII**: Verify zero hardcoded credentials, tokens, private keys, or PII fields (`ssn`, `cardNumber`, `password`) in code, tests, or log statements.
- **Injection & AuthZ**: Verify parameterized SQL queries, validated request schemas, and explicit tenant/user ownership checks (`403` on cross-tenant IDs).
- **Anti-Slopsquatting Check**: If `package.json`, `pom.xml`, `build.gradle`, or `requirements.txt` changed, verify every added package is explicitly approved, `>= 30 days` old in the public registry, and has valid provenance signatures.

## 3. Architecture & Concurrency Audit
- **Transaction Boundaries**: Flag DB transactions (`@Transactional` / `BEGIN`) held open across external HTTP/gRPC calls or message broker publishes.
- **Query Scalability**: Flag unbounded `SELECT *` queries missing pagination or `N+1` loop queries.
- **Resource & Thread Safety**: Flag blocking I/O on event loops / virtual-thread `synchronized` pinning, unclosed streams, or missing HTTP timeouts.

## 4. Structured Review Output Format
Output a concise PR triage table categorized as:
- 🔴 **`[BLOCKER]`** — Must be fixed before human review is requested (security flaw, broken contract, missing test, transaction bug).
- 🟡 **`[WARNING]`** — Architectural trade-off or edge case for the human `CODEOWNERS` reviewer to inspect.
- 🟢 **`[VERIFIED]`** — Summary of passing deterministic gates and coverage delta.
