# Enterprise AI-Embedded SDLC & Coding Harness (`sdlc-skills`)

Production-ready **Enterprise AI-Embedded SDLC Governance & Coding Harness** packages aligned with Gartner's **5-Stage AI Engineering Maturity Model**, **Harness Engineering**, and **Contract-First Subagent Orchestration**.

---

## 📦 Repository Contents (The Three Skill Editions)

### 1. [`enterprise-universal-skills/`](./enterprise-universal-skills/) ([`enterprise-universal-skills.zip`](./enterprise-universal-skills.zip))
Language- and domain-agnostic **Universal Enterprise Skills Library** + 35-line [`AGENTS.template.md`](./enterprise-universal-skills/AGENTS.template.md) that can be dropped into any microservice, frontend, or legacy monolith repository in `<10 minutes`:
- [`01-spec-and-ambiguity-gate`](./enterprise-universal-skills/01-spec-and-ambiguity-gate/SKILL.md) — Untrusted ticket airlock (`OWASP LLM01`), blast-radius check, and mandatory `ask_question` mechanical turn-halt before coding.
- [`02-contract-first-subagents`](./enterprise-universal-skills/02-contract-first-subagents/SKILL.md) — Shared DTO/API contract lock + `ask_question` human approval gate + parallel subagent dispatch with disjoint file ownership.
- [`03-brownfield-safe-refactor`](./enterprise-universal-skills/03-brownfield-safe-refactor/SKILL.md) — Golden-Master characterization tests, Strangler Fig isolation, `>= 85%` differential coverage ratchet, and runtime module smoke check.
- [`04-adversarial-test-gate`](./enterprise-universal-skills/04-adversarial-test-gate/SKILL.md) — 4-quadrant adversarial test generation and Assertion Integrity Guard (blocks `.skip` / deleted assertions).
- [`05-db-expand-and-contract`](./enterprise-universal-skills/05-db-expand-and-contract/SKILL.md) — Zero-downtime 3-release Expand-Migrate-Contract SQL protocol for safe `<60s` container rollbacks.
- [`06-pre-pr-security-review`](./enterprise-universal-skills/06-pre-pr-security-review/SKILL.md) — Pre-PR OWASP Top 10, anti-slopsquatting dependency check, and concurrency/transaction boundary review.
- [`07-incident-rca-and-hotfix`](./enterprise-universal-skills/07-incident-rca-and-hotfix/SKILL.md) — Deterministic rollback first, crash log sanitization airlock, root-cause correlation, and reproduction-first hotfix PR.

---

### 2. [`typescript-edition/`](./typescript-edition/) ([`ai-sdlc-director-package-typescript.zip`](./ai-sdlc-director-package-typescript.zip))
Full **TypeScript / React / Node.js Director Package** (`99.5 / 100` Verifier Audit Score), including:
- Executive Summary & Gartner Strategy (`01-EXECUTIVE-SUMMARY-AND-GARTNER-STRATEGY.md`)
- 7-Phase SDLC Gate Specifications (`02-SDLC-GATE-SPECIFICATIONS.md`)
- Minimalist Harness & Skill Standards (`03-MINIMALIST-HARNESS-AND-SKILL-STANDARDS.md`)
- AI Platform Engineer Charter & 90-Day Rollout Plan (`04-AI-PLATFORM-ENGINEER-CHARTER-AND-90-DAY-ROLLOUT.md`)
- Verifier Audit Report (`05-VERIFIER-AUDIT-REPORT.md`)
- Real Brownfield Case Study on `dynamicStats` (`06-DYNAMICSTATS-BROWNFIELD-CASE-STUDY.md`)
- Reference TypeScript/React Harness (`reference-harness-weather-app/`) & `enterprise-universal-skills/`

---

### 3. [`java-springboot-edition/`](./java-springboot-edition/) ([`ai-sdlc-director-package-java-springboot.zip`](./ai-sdlc-director-package-java-springboot.zip))
Full **Java 21 / Spring Boot 3.x Director Package** (`99.5 / 100` Verifier Audit Score), tailored for enterprise JVM microservices:
- Enforces Maven/Gradle (`spotless`, `checkstyle`, `ArchUnit`, `Testcontainers`, `diff-cover` with JaCoCo, and Flyway `squawk` linting)
- Virtual Thread (`spring.threads.virtual.enabled=true`) non-pinning & `@Transactional` boundary gates
- Reference Spring Boot 3.3 Service Harness (`reference-harness-spring-weather-service/`) & `enterprise-universal-skills/`

---

## 📊 Executive Presentation & Brownfield Case Study
- [`AI-SDLC-Director-Presentation.html`](./AI-SDLC-Director-Presentation.html) — Interactive HTML5 Executive Presentation Deck
- [`AI-SDLC-Director-Presentation.pptx`](./AI-SDLC-Director-Presentation.pptx) — Native PowerPoint Presentation Deck
- [`DYNAMICSTATS-SDLC-CASE-STUDY.md`](./DYNAMICSTATS-SDLC-CASE-STUDY.md) — End-to-End Brownfield Validation Report (`0% -> 100%` critical-path test coverage, `26/26` passing Vitest tests)
