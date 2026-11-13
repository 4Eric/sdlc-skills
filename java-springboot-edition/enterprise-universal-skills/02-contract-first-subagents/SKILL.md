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

# Phase 2 & 3: Contract-First Subagent Orchestration Skill

## Iron Law
**Never spawn coding subagents or implement multi-layer business logic until shared contracts are locked, typechecked, and approved via `ask_question`. Violating the letter of the rules IS violating the spirit of the rules.**

## Workflow Modes

### Fast-Path (`<= 3 files`, single API call)
- If both Spec and API Contract were already signed off in `01-spec-and-ambiguity-gate` (Mode A), skip subagent fan-out.
- Implement types and client/service in a single agent, run the Closed-Loop Verification Cycle (`Compile -> Test -> Runtime Smoke Check`), and pass the baton to `04-adversarial-test-gate`.

### Full Enterprise Path (`> 3 files` or multi-layer changes)

#### Step 1: Phase 2 — Lock the Shared Contract First (`pro` Model)
Never spawn parallel coding subagents until the shared data/API contract is written and approved:
1. Update ONLY the contract files defined in `AGENTS.md` (e.g., `src/types/*.ts`, `src/.../dto/*Record.java`, `openapi.yaml`, or `.proto`).
2. Run the repo's **Compile & Typecheck** command from `AGENTS.md` to verify contract syntax.
3. **Mechanical Turn-Halt (Contract Sign-Off)**: Present the `<50-line` contract diff and invoke `ask_question` to obtain human approval before dispatching Phase 3 implementation subagents.

#### Step 2: Phase 3 — Dispatch Parallel Subagents with Disjoint File Ownership
Once the Phase 2 contract is approved, freeze the contract files (read-only) and assign each subagent a non-overlapping file write scope:

| Subagent Role | Model Tier | Allowed Write Scope | Prohibited Actions |
| :--- | :---: | :--- | :--- |
| **Data / Service Agent** | `flash` | `service/`, `repository/`, `client/` | Editing contracts, controllers/UI, or dependency manifests |
| **API / UI Agent** | `pro` | `controller/`, `components/`, `views/` | Editing contracts, service layer, or test config |
| **Adversarial Test Agent** | `flash` | `src/test/`, `src/__tests__/` | Editing production source code to make tests pass |

#### Step 3: Dependency Manifest Lock (`Anti-Slopsquatting`)
- Subagents are strictly forbidden from running `npm install <pkg>`, `pip install <pkg>`, or modifying `package.json` / `pom.xml` / `build.gradle` unless the exact package name and version were explicitly approved by the human in the Phase 1/2 `ask_question` gate.

#### Step 4: Phase 3 Exit Gate (Max 3 Self-Heal Retries)
Run the repository's **Compile & Typecheck**, **Format & Static Lint**, and **Runtime Smoke Check** commands from `AGENTS.md`.
- All must exit with code `0` (`0` errors, `0` warnings).
- If compilation or startup fails after **3 automated repair attempts**, halt and call `ask_question` to escalate to the human engineer.

## Common Rationalizations
| Excuse | Reality |
| :--- | :--- |
| "Faster to code types and service together." | Unlocked contracts cause subagent race conditions and mismatched DTO fields. |
| "Emergency fix, skip contract review." | Rushed contracts permanently break downstream API consumers. |
| "I already drafted the service code." | Sunk cost fallacy. Stash the code and lock the contract first. |
| "Types are obvious from context." | "Obvious" types diverge across parallel subagents. Lock them explicitly. |

## Red Flags - STOP
- Spawning coding subagents before contract sign-off via `ask_question`.
- Assigning multiple subagents write access to the same file or directory.
- Modifying `package.json` / `pom.xml` with unapproved dependencies.
- Subagents modifying locked contract files during Phase 3 implementation.

**All of these mean: STOP immediately. Delete any unapproved edits or weakened tests. Start over.**

## Valid Exceptions (When NOT to Use)
- Fast-Path edits (`<= 3 files`) already signed off in `01-spec-and-ambiguity-gate`.
- Read-only analysis or internal bugfixes that do not alter any DTO, interface, or schema.
- Test-only updates that do not touch production contracts.

## Baton-Pass Chaining (REQUIRED NEXT SKILL)
- Once Phase 3 compilation, lint, and runtime smoke checks pass, automatically load `04-adversarial-test-gate`.
