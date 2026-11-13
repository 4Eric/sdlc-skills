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

## Workflow Modes

### Fast-Path (`<= 3 files`, localized extraction)
- Audit legacy target (read-only) and present `<40-line` Refactor & Characterization Plan; halt via **ONE `ask_question`**.
- Write Characterization ("Golden Master") tests capturing current behavior; verify `100%` pass before refactoring.
- Extract clean module (Strangler Fig) and run Closed-Loop (`Compile -> Test -> Runtime Smoke Check`).
- Pass baton to `04-adversarial-test-gate`.

### Full Enterprise Path (`> 3 files` or build/config changes like `tsconfig.json` / `pom.xml`)
- Step 0: Audit & halt via `ask_question` for approval on extraction scope and build/test runner configs.
- Step 1: Spawn `flash` Test Subagent to lock existing behavior with Characterization Tests (`100%` green on unmodified code).
- Step 2: Spawn `pro` Refactor Subagent to extract isolated module (`>= 85%` differential coverage on modified lines).
- Step 3: Execute **Runtime Smoke Check** (`npm run start` / `ts-node` / `spring-boot:run`) to verify CJS/ESM/classpath startup.

## Common Rationalizations
| Excuse | Reality |
| :--- | :--- |
| "I'll write tests after refactoring." | Post-refactor tests only assert your new bugs, not original behavior. |
| "Vitest passed, skip `npm run start`." | Test runners mock module resolution; runtime `ts-node`/Node often crashes (`ERR_MODULE_NOT_FOUND`). |
| "Global coverage is 10%, skip coverage gate." | Use the Brownfield Ratchet: `>= 85%` differential coverage on touched lines. |
| "User said 'exercise SDLC on this repo'." | Still requires Phase 0 `ask_question` sign-off before editing legacy files. |

## Red Flags - STOP
- Editing legacy logic before Characterization Tests exist and pass.
- Adding `tsconfig.json` or `package.json` changes without running the runtime start command.
- Inlining new features into a 300+ line legacy method instead of Strangler Fig extraction.
- Skipping `ask_question` approval before touching legacy files.

**All of these mean: STOP immediately. Delete any unapproved edits or weakened tests. Start over.**

## Valid Exceptions (When NOT to Use)
- Greenfield modules already covered by `>= 80%` unit tests (use `01` & `02` instead).
- Dead-code deletion verified by zero compiler/grep references.
- Comment or documentation-only updates.

## Baton-Pass Chaining (REQUIRED NEXT SKILL)
- Automatically load `04-adversarial-test-gate` to verify differential coverage and edge-case resilience on the extracted module.
