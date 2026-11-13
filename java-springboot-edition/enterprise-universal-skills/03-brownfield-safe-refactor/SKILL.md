---
name: 03-brownfield-safe-refactor
description: Use when refactoring legacy code, touching modules with <50% test coverage, or modifying high-complexity monolith files before editing production logic.
metadata:
  category: discipline
  triggers:
    - legacy refactor
    - low test coverage
    - monolith cleanup
    - untestable code
    - high cyclomatic complexity
    - brownfield modification
---

# Brownfield & Legacy Monolith Safe-Refactor Skill

## Iron Law
**Never modify legacy production code until a Refactor Plan is approved via `ask_question` and Golden-Master Characterization Tests pass 100%. Violating the letter of the rules IS violating the spirit of the rules.**

## 1. Why Global Coverage Gates Fail on Legacy Code
Enforcing an `85%` global repository coverage gate on a 10-year-old monolith with `20%` baseline coverage deadlocks the CI pipeline. Instead, enforce the **Brownfield Ratchet**:
- **Differential Coverage (`diff-cover`)**: `>= 85%` branch/line coverage strictly on lines added or modified in the current `git diff`.
- **Global Ratchet**: Overall repository coverage must be `>= baseline` (never decrease).

## 2. Step 0: Audit & Mechanical Turn-Halt (`ask_question`) Before Touching Code
1. Audit the legacy repository (read-only) to identify untestable inline logic, missing build/test configs (`tsconfig.json`, `vitest`, `junit`), and schema risks.
2. **HARD STOP RULE**: Present a concise Brownfield Refactor Plan (listing exact files to extract, characterization tests to add, and any `package.json`/`tsconfig.json`/`pom.xml` changes) and **MUST invoke `ask_question`** to obtain human approval before modifying any code or installing test runners.
   - *Fast-Path (`<= 3 files`, localized extraction)*: Single agent executes Steps 1–3 after this one `ask_question` sign-off.
   - *Full Path (`> 3 files` or build config additions)*: Dispatch dedicated Test (`flash`) and Refactor (`pro`) subagents.

## 3. Step 1: Mandatory Characterization ("Golden Master") Tests (`flash`)
After human sign-off, before changing legacy business logic:
1. Spawn a `flash` Test Subagent (or write directly in Fast-Path) to create **Characterization Tests** (snapshot / Golden Master / `MockMvc` / approval tests) that capture the current actual behavior of the legacy method—including its quirks and edge cases.
2. Run the test suite and verify the characterization tests pass `100%` against the unmodified legacy code.

## 4. Step 2: Strangler Fig Isolation (`pro`)
1. Do not inline new business logic into a 500-line legacy method.
2. Extract the new behavior into a clean, well-typed class/module behind a narrow interface (Strangler Fig pattern) and delegate from the legacy entry point.

## 5. Step 3: Verification, Runtime Smoke Check & Ratchet Gate
1. Verify all **Step 1 Characterization Tests** still pass (proving zero regressions in untouched legacy paths).
2. Verify unit tests on the newly extracted module achieve **`>= 85%` differential coverage** and cyclomatic complexity decreased or remained flat.
3. **Runtime Module Compatibility Check**: If `tsconfig.json`, `package.json`, or build configs were added/modified, execute the repository's runtime start command (`npm run start` / `ts-node` / `./mvnw spring-boot:run`) to confirm module resolution (`CommonJS` vs `ESM`) works at runtime, not just under the test runner.

## 6. Common Rationalizations
| Excuse | Reality |
| :--- | :--- |
| "I'll write tests after refactoring." | Post-refactor tests only assert your new bugs, not original behavior. |
| "Vitest passed, skip `npm run start`." | Test runners mock module resolution; runtime `ts-node`/Node often crashes (`ERR_MODULE_NOT_FOUND`). |
| "Global coverage is 10%, skip coverage gate." | Use the Brownfield Ratchet: `>= 85%` differential coverage on touched lines. |
| "User said 'exercise SDLC on this repo'." | Still requires Step 0 `ask_question` sign-off before editing legacy files. |

## 7. Red Flags - STOP
- Editing legacy logic before Characterization Tests exist and pass.
- Adding `tsconfig.json` or `package.json` changes without running the runtime start command.
- Inlining new features into a 300+ line legacy method instead of Strangler Fig extraction.
- Skipping `ask_question` approval before touching legacy files.

**All of these mean: STOP immediately. Delete any unapproved edits or weakened tests. Start over.**

## 8. Valid Exceptions (When NOT to Use)
- Greenfield modules already covered by `>= 80%` unit tests (use `01` & `02` instead).
- Dead-code deletion verified by zero compiler/grep references.
- Comment or documentation-only updates.

## 9. Baton-Pass Chaining (REQUIRED NEXT SKILL)
- Automatically load `04-adversarial-test-gate` to verify differential coverage and edge-case resilience on the extracted module.
