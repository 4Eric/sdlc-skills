---
name: 02-contract-first-subagents
description: Universal Phase 2 & 3 SDLC skill. Locks shared interfaces first, halts via ask_question for human contract sign-off, and dispatches parallel subagents with disjoint file scopes.
last_verified: 2026-10-31
---

# Phase 2 & 3: Contract-First Subagent Orchestration Skill

Use this skill only AFTER the human has approved the Phase 1 Spec via `ask_question`.

## Step 1: Phase 2 — Lock the Shared Contract First (`pro` Model)
Never spawn parallel coding subagents until the shared data/API contract is written and approved:
1. Update ONLY the contract files defined in `AGENTS.md` (e.g., `src/types/*.ts`, `src/.../dto/*Record.java`, `openapi.yaml`, or `.proto`).
2. Run the repo's **Compile & Typecheck** command from `AGENTS.md` to verify contract syntax.
3. **Mechanical Turn-Halt (Contract Sign-Off)**: Present the `<50-line` contract diff and invoke `ask_question` to obtain human approval before dispatching Phase 3 implementation subagents.

## Step 2: Phase 3 — Dispatch Parallel Subagents with Disjoint File Ownership
Once the Phase 2 contract is approved, freeze the contract files (read-only) and assign each subagent a non-overlapping file write scope:

| Subagent Role | Model Tier | Allowed Write Scope | Prohibited Actions |
| :--- | :---: | :--- | :--- |
| **Data / Service Agent** | `flash` | `service/`, `repository/`, `client/` | Editing contracts, controllers/UI, or dependency manifests |
| **API / UI Agent** | `pro` | `controller/`, `components/`, `views/` | Editing contracts, service layer, or test config |
| **Adversarial Test Agent** | `flash` | `src/test/`, `src/__tests__/` | Editing production source code to make tests pass |

## Step 3: Dependency Manifest Lock (`Anti-Slopsquatting`)
- Subagents are strictly forbidden from running `npm install <pkg>`, `pip install <pkg>`, or modifying `package.json` / `pom.xml` / `build.gradle` unless the exact package name and version were explicitly approved by the human in the Phase 1/2 `ask_question` gate.

## Step 4: Phase 3 Exit Gate (Max 3 Self-Heal Retries)
Run the repository's **Compile & Typecheck** and **Format & Static Lint** commands from `AGENTS.md`.
- Both must exit with code `0` (`0` errors, `0` warnings).
- If compilation fails after **3 automated repair attempts**, halt and call `ask_question` to escalate to the human engineer.
