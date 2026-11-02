---
name: 03-brownfield-safe-refactor
description: Universal enterprise skill for safely modifying legacy monoliths or low-coverage (<50%) modules using Golden-Master Characterization Tests, mechanical ask_question sign-off, and Differential Coverage ratchets.
last_verified: 2026-11-02
---

# Brownfield & Legacy Monolith Safe-Refactor Skill

Use this skill whenever modifying a legacy repository or module where existing test coverage is below `50%` or cyclomatic complexity is high.

## 1. Why Global Coverage Gates Fail on Legacy Code
Enforcing an `85%` global repository coverage gate on a 10-year-old monolith with `20%` baseline coverage deadlocks the CI pipeline. Instead, enforce the **Brownfield Ratchet**:
- **Differential Coverage (`diff-cover`)**: `>= 85%` branch/line coverage strictly on lines added or modified in the current `git diff`.
- **Global Ratchet**: Overall repository coverage must be `>= baseline` (never decrease).

## 2. Step 0: Audit & Mechanical Turn-Halt (`ask_question`) Before Touching Code
1. Audit the legacy repository (read-only) to identify untestable inline logic, missing build/test configs (`tsconfig.json`, `vitest`, `junit`), and schema risks.
2. **HARD STOP RULE**: Present a concise Brownfield Refactor Plan (listing exact files to extract, characterization tests to add, and any `package.json`/`tsconfig.json`/`pom.xml` changes) and **MUST invoke `ask_question`** to obtain human approval before modifying any code or installing test runners.

## 3. Step 1: Mandatory Characterization ("Golden Master") Tests (`flash`)
After human sign-off, before changing legacy business logic:
1. Spawn a `flash` Test Subagent to write **Characterization Tests** (snapshot / Golden Master / `MockMvc` / approval tests) that capture the current actual behavior of the legacy method—including its quirks and edge cases.
2. Run the test suite and verify the characterization tests pass `100%` against the unmodified legacy code.

## 4. Step 2: Strangler Fig Isolation (`pro`)
1. Do not inline new business logic into a 500-line legacy method.
2. Extract the new behavior into a clean, well-typed class/module behind a narrow interface (Strangler Fig pattern) and delegate from the legacy entry point.

## 5. Step 3: Verification, Runtime Smoke Check & Ratchet Gate
1. Verify all **Step 1 Characterization Tests** still pass (proving zero regressions in untouched legacy paths).
2. Verify unit tests on the newly extracted module achieve **`>= 85%` differential coverage**.
3. **Runtime Module Compatibility Check**: If `tsconfig.json`, `package.json`, or build configs were added/modified, execute the repository's runtime start command (`npm run start` / `ts-node` / `./mvnw spring-boot:run`) to confirm module resolution (`CommonJS` vs `ESM`) works at runtime, not just under the test runner.
