---
name: 02-contract-first-subagents
description: Use when Phase 1 spec is approved, multiple subagents are needed, shared DTOs are modified, or coordinating multi-file changes.
metadata:
  category: discipline
  triggers:
    - phase 1 approved
    - shared interface changes
    - multi-agent coordination
    - dto contract lock
    - parallel coding subagents
    - microservice boundaries
---

# Phase 2 & 3: Contract-First Subagents

## Iron Law
**Never spawn coding subagents or implement business logic until shared contracts are locked, typechecked, and approved via `ask_question`. Violating the letter of the rules IS violating the spirit of the rules.**

## Workflow Modes

### Fast-Path (`<= 3 files`)
- If approved in `01`, skip subagent dispatch.
- Single agent updates types and implementation.
- Run Closed-Loop (`Compile -> Test -> Smoke`).
- Pass baton to `04`.

### Full Enterprise Path (`> 3 files` or multi-layer)
- **Phase 2 Contract Lock (`pro`)**:
  - Update ONLY contract files (`src/types/`, `dto/`).
  - Run `Compile & Typecheck`.
  - Present `<50-line` diff; halt via `ask_question`.
- **Phase 3 Disjoint Subagents**:
  - Freeze contracts (read-only).
  - Assign non-overlapping scopes:
    - *Service (`flash`)*: `service/`, `repository/`.
    - *UI (`pro`)*: `controller/`, `views/`.
    - *Test (`flash`)*: `src/test/` only.
- **Closed-Loop Exit**: Run `Compile` + `Lint`. Max 3 retries before `ask_question` halt.
- **Anti-Slopsquatting**: No unapproved dependencies.

## Common Rationalizations
| Excuse | Reality |
| :--- | :--- |
| "Faster to code together." | Unlocked contracts cause subagent race conditions. |
| "Emergency fix, skip review." | Rushed contracts permanently break APIs. |
| "Already drafted service code." | Sunk cost fallacy. Stash code, lock contract first. |
| "Types obvious from context." | Obvious types diverge across subagents. Lock them. |

## Red Flags - STOP
- Spawning subagents before contract sign-off via `ask_question`.
- Multiple subagents writing to the same file.
- Modifying manifests with unapproved dependencies.
- Subagents modifying locked contracts in Phase 3.

**All of these mean: STOP immediately. Delete any unapproved edits or weakened tests. Start over.**

## Valid Exceptions (When NOT to Use)
- Fast-Path edits signed off in `01`.
- Read-only analysis without schema changes.
- Test-only updates not touching contracts.

## Baton-Pass Chaining (REQUIRED NEXT SKILL)
- Automatically load `04-adversarial-test-gate`.
