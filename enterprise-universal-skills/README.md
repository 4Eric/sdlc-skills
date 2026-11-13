# Enterprise Universal AI Skills Library (`enterprise-universal-skills`)

> **100% App-Agnostic:** These 7 enterprise SDLC gate skills apply to **every repository in the company**—whether it is a payments microservice, an order-management monolith, a customer web portal, or an internal data pipeline—and are certified `100/100` against both **Gartner AI-Embedded SDLC Governance** and the **Official Agent Skills Specification (`writing-skills` / CSO / Anti-Rationalization)**.

---

## How the 2-Layer Enterprise Harness Works

To prevent duplicating skills across 50+ repositories, separate **Company-Wide SDLC Workflows** from **Repo-Specific Context**:

```
┌─────────────────────────────────────────────────────────────────────────┐
│ LAYER 1: Universal Enterprise Skills (Shared Across ALL Repositories)   │
│ Installed once from central Git repo: ~/.gemini/config/skills/          │
│                                                                         │
│  01-spec-and-ambiguity-gate      (Phase 1: Requirements & Blast Radius) │
│  02-contract-first-subagents     (Phase 2-3: Multi-Agent Orchestration) │
│  03-brownfield-safe-refactor     (Phase 3-4: Legacy Monolith Ratchet)   │
│  04-adversarial-test-gate        (Phase 4: Independent Test & Guard)    │
│  05-db-expand-and-contract       (Phase 2/6/7: Zero-Downtime SQL Gate)  │
│  06-pre-pr-security-review       (Phase 5-6: Code Review & Slopsquat)   │
│  07-incident-rca-and-hotfix      (Phase 7: Production Triage & Fix)     │
└───────────────────────────────────┬─────────────────────────────────────┘
                                    │ Reads repo commands & boundaries from
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│ LAYER 2: Repo-Specific AGENTS.md (1 File at Root of Each Repository)    │
│ Defines ONLY: Build/Test CLI commands, folder map, & internal SDK rules │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## How Developers Use This Naturally (Zero Skill-Number Memorization)

1. **One-Time Setup (`< 10 mins`)**:
   - Copy `01-spec-and-ambiguity-gate` through `07-incident-rca-and-hotfix` into `<repo>/.agents/skills/` (or globally into `~/.gemini/config/skills/`).
   - Copy [`AGENTS.template.md`](./AGENTS.template.md) to `<repo>/AGENTS.md` and fill in your build/test/start commands.
2. **Natural Prompting with Automatic Baton-Pass Chaining**:
   - Because every skill uses **CSO Trigger-Only descriptions (`Use when...`)** and **`REQUIRED NEXT SKILL` baton-pass handoffs**, developers simply prompt naturally:
     - *"Build a spec and call the Stripe & Slack APIs"* → Triggers **Fast-Path (`<= 3 files`)**: **1 combined Spec+Contract approval (`ask_question`)** → Closed-Loop implementation (`Compile -> Test -> Runtime Smoke Check`) → `04` Adversarial Test → `06` Pre-PR check.
     - *"Add a multi-module order fulfillment workflow"* → Triggers **Full Enterprise Path (`> 3 files`)**: `01` Spec (`ask_question`) → `02` Contract Lock (`ask_question`) & Parallel Subagents → `04` Adversarial Test → `06` Pre-PR check.
     - *"Refactor this untested legacy file"* → Automatically routes `03-brownfield-safe-refactor` → `04` → `06`.
     - *"Rename column in PostgreSQL"* → Automatically routes `05-db-expand-and-contract`.
     - *"Production 500 error in checkout"* → Automatically routes `07-incident-rca-and-hotfix` (`<60s` rollback first → reproduction test → surgical fix) → `04` → `06`.

---

## Directory Index

| Skill Folder | SDLC Phase | Works With | Purpose |
| :--- | :--- | :--- | :--- |
| **[`AGENTS.template.md`](./AGENTS.template.md)** | All Phases | Any Stack | Drop-in root template for any microservice or frontend repo (includes FinOps routing & SDD Closed-Loop Router). |
| **[`01-spec-and-ambiguity-gate/SKILL.md`](./01-spec-and-ambiguity-gate/SKILL.md)** | Phase 1 | Any Stack | OWASP LLM01 ticket airlock, Fast-Path vs. Full-Path sizing, `<15` file blast-radius check, and mandatory `ask_question` spec gate. |
| **[`02-contract-first-subagents/SKILL.md`](./02-contract-first-subagents/SKILL.md)** | Phases 2 & 3 | Any Stack | Locks DTO/interface contracts first (`ask_question`), then dispatches parallel `flash`/`pro` subagents with disjoint file scopes. |
| **[`03-brownfield-safe-refactor/SKILL.md`](./03-brownfield-safe-refactor/SKILL.md)** | Phases 3 & 4 | Any Stack | Writes Characterization ("Golden Master") tests before modifying legacy `<50%` coverage code; enforces `diff-cover >= 85%` & runtime smoke check. |
| **[`04-adversarial-test-gate/SKILL.md`](./04-adversarial-test-gate/SKILL.md)** | Phase 4 | Any Stack | Independent 4-quadrant test generation + absolute Assertion Integrity Guard (forbids `.skip` / `@Disabled` or deleting assertions). |
| **[`05-db-expand-and-contract/SKILL.md`](./05-db-expand-and-contract/SKILL.md)** | Phases 2, 6, 7 | Any SQL DB | Enforces 3-release Expand-Migrate-Contract schema changes so `<60s` app rollbacks never fail against `N-1` pods. |
| **[`06-pre-pr-security-review/SKILL.md`](./06-pre-pr-security-review/SKILL.md)** | Phases 5 & 6 | Any Stack | First-pass PR diff audit: OWASP Top 10, anti-slopsquatting dependency check, concurrency/transaction traps, `<400 LOC` cap. |
| **[`07-incident-rca-and-hotfix/SKILL.md`](./07-incident-rca-and-hotfix/SKILL.md)** | Phase 7 | Any Stack | `<60s` rollback first, sanitizes untrusted logs, correlates stack traces with recent commits, and produces a reproduction test + minimal hotfix PR. |
