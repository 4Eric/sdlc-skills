# Universal Enterprise AI-SDLC Skills Library (`enterprise-universal-skills`)

App-agnostic, language-agnostic **SDD (Spec-Driven Development) & Closed-Loop Engineering** skills built and verified against the Official Agent Skills Specification (`writing-skills` / CSO / Anti-Rationalization).

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

## Included Files
- [`AGENTS.template.md`](./AGENTS.template.md) — `<45-line` repository harness template with the **Natural SDD & Closed-Loop Router**.
- [`01-spec-and-ambiguity-gate/SKILL.md`](./01-spec-and-ambiguity-gate/SKILL.md) — Intake airlock, Fast-Path vs. Full-Path sizing, and mechanical `ask_question` spec gate.
- [`02-contract-first-subagents/SKILL.md`](./02-contract-first-subagents/SKILL.md) — Shared DTO/API contract lock and disjoint parallel subagent dispatch.
- [`03-brownfield-safe-refactor/SKILL.md`](./03-brownfield-safe-refactor/SKILL.md) — Golden-Master characterization testing, Strangler Fig isolation, `>= 85%` diff-cover ratchet, and runtime boot check.
- [`04-adversarial-test-gate/SKILL.md`](./04-adversarial-test-gate/SKILL.md) — 4-quadrant adversarial testing and absolute Assertion Integrity Guard.
- [`05-db-expand-and-contract/SKILL.md`](./05-db-expand-and-contract/SKILL.md) — 3-release Expand-Migrate-Contract zero-downtime SQL protocol.
- [`06-pre-pr-security-review/SKILL.md`](./06-pre-pr-security-review/SKILL.md) — OWASP Top 10, anti-slopsquatting dependency lock, and `<400 LOC` review gate.
- [`07-incident-rca-and-hotfix/SKILL.md`](./07-incident-rca-and-hotfix/SKILL.md) — `<60s` rollback-first rule, log sanitization airlock, and reproduction-first surgical hotfix.
