---
name: 04-adversarial-test-gate
description: Universal Phase 4 SDLC skill. Enforces independent adversarial test generation and the Assertion Integrity Guard (preventing AI from weakening tests to pass CI).
last_verified: 2026-10-30
---

# Phase 4: Adversarial Testing & Assertion Integrity Skill

Use this skill to generate and verify tests for any feature or bugfix.

## 1. Independent Test Generation (`flash` Model)
Tests must be written against the **Phase 2 Contract and Specification**, not by copying the implementation's internal bugs. Every test suite must cover 4 mandatory quadrants:
1. **Happy Path**: Valid inputs produce expected contract outputs and side effects.
2. **Boundary & Edge Cases**: `0`, negative numbers, empty strings/arrays, `null`/`undefined`, max payload sizes, timezone/leap-year boundaries.
3. **Upstream Failure & Resilience**: Simulated HTTP `429`/`500`/`503` errors, timeouts, malformed responses, and circuit-breaker fallbacks.
4. **Security & Authorization**: Unauthenticated requests (`401`), unauthorized cross-tenant access (`403`), and invalid schema payloads (`400`).

## 2. The Assertion Integrity Guard (Hard Red Line)
An AI agent is **strictly prohibited** from making a failing test suite pass by weakening the tests:
- ⛔ **NEVER** add `.skip`, `.todo`, `@Disabled`, `@Ignore`, or `@pytest.mark.skip` to existing tests.
- ⛔ **NEVER** delete an existing test case or reduce the number of assertions (`expect(...)` / `assertThat(...)`) without explicit human sign-off.
- ⛔ **NEVER** replace specific value assertions with tautologies (e.g., replacing `expect(res.status).toBe(200)` with `expect(res).toBeDefined()`).
- ⛔ **NEVER** modify production business logic inside a Test Subagent turn—if a test exposes a real bug in Phase 3 code, report the failure back to the Implementation Agent.

## 3. Deterministic Exit Gate
Execute the repository's **Unit & Slice Tests** and **Differential Coverage Gate** commands from `AGENTS.md`:
- `100%` of tests must pass (`0` failures, `0` skipped).
- Modified/added code must meet **`>= 85%` branch/line coverage**.
