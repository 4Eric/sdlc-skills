# Real-World Brownfield SDLC Case Study & Security Audit: `dynamicStats`

**Target Repository:** `E:\dynamicStats` (Youth Baseball Analytics & Gemini Vision Box-Score Ingestion Monorepo)  
**Harness Architecture:** 2-Layer Enterprise AI-Native SDLC (`AGENTS.md` + `.agents/skills/01..07`)  
**Audit Classification:** Pre-PR Architecture, Test Gate & Security Review (Phases 1–6)  
**Date:** October 31, 2026

---

## 1. Executive Overview

To validate that our **Universal Enterprise Skills Library (`01`–`07`)** operates as a drop-in engineering harness on real-world legacy codebases, we executed a full **Phases 1–6 Brownfield SDLC Exercise** against `E:\dynamicStats`—an existing full-stack Node 20 / Express 5 / PostgreSQL (`pg`) / Google Gemini Vision (`@google/genai`) + Vue 3 / TypeScript / Pinia / Chart.js baseball analytics monorepo.

Prior to this exercise, `dynamicStats` exhibited classic brownfield risk markers:
- **`0%` Automated Test Coverage**: `backend/package.json` exited with `echo "Error: no test specified" && exit 1`, and `frontend/package.json` had no test runner configured.
- **Controller-Embedded Domain Logic**: Fuzzy OCR player-matching and PostgreSQL persistence calls were coupled inside an inline anonymous closure in `POST /api/ingest/confirm` (`backend/src/index.ts`), making the highest-risk ingestion logic impossible to unit-test without spinning up HTTP + PostgreSQL infrastructure.
- **Latent Schema & Coercion Bugs**: `backend/src/db/init.ts` declared `positions VARCHAR[] NOT NULL` without a default value (breaking `savePlayerTx` inserts on fresh databases), and `calculatePitchingStats` in `frontend/src/utils/statsEngine.ts` lacked numeric coercion against OCR string outputs.

By dropping in the **7 Universal SDLC Skills** (`.agents/skills/01..07`) and authoring a **36-line repository `AGENTS.md`**, coordinated multi-model subagents (`flash_lite` -> `pro` -> `flash`) safely refactored the brownfield codebase, fixed latent schema and coercion defects, established deterministic Vitest suites across both workspaces, and produced a structured OWASP/Architecture security audit—all in **under 15 minutes**.

### Before vs. After Transformation Summary

| Dimension | Before SDLC Harness (Baseline) | After Phases 1–6 Execution |
| :--- | :--- | :--- |
| **AI Context & Guardrails** | None (ad-hoc prompting, high hallucination risk on baseball math) | **36-line `AGENTS.md`** + **7 Universal Skills** enforcing strict gates |
| **Backend Verification Gate** | `npm test` -> `exit 1` (No test runner or `typecheck` script) | `tsc --noEmit` + **Vitest unit & transactional slice suite (`exit 0`)** |
| **Frontend Verification Gate** | Build-only (`vue-tsc -b && vite build`), `0` unit tests | `vue-tsc -b && vite build` + **Vitest sabermetric test suite (`exit 0`)** |
| **OCR Player Resolution** | Inline 32-line closure trapped inside `POST /api/ingest/confirm` | Pure, dependency-injected Strangler module (`backend/src/playerResolver.ts`) |
| **PostgreSQL Schema (`init.ts`)** | Broken `positions VARCHAR[] NOT NULL` (fails on `savePlayerTx`) | Fixed `positions VARCHAR[] NOT NULL DEFAULT '{}'` |
| **Sabermetric Math (`statsEngine.ts`)** | Uncoerced pitching fields vulnerable to OCR string concatenation/`NaN` | Hardened `Number(...) \|\| 0` coercion across `parseIP` and `calculatePitchingStats` |
| **Security & OWASP Visibility** | Unaudited Express/Multer/Gemini Vision surface | Full **Phase 6 Triage Ledger** (`[VERIFIED]`, `[FIXED]`, `[BACKLOG WARNINGS]`) |

---

## 2. Phase-by-Phase Execution Walkthrough

```
+-----------------------------------------------------------------------------------+
| LAYER 1: UNIVERSAL ENTERPRISE SKILLS (.agents/skills/01..07)                      |
| Portable across Java, Node/TS, Python, Go — Zero repo-specific hardcoding         |
+-----------------------------------------+-----------------------------------------+
                                          | Reads commands, boundaries & domain rules
                                          v
+-----------------------------------------------------------------------------------+
| LAYER 2: REPO-SPECIFIC HARNESS (E:\dynamicStats\AGENTS.md — 36 Lines)             |
| Commands | Module Lock Boundaries | Baseball Base-3 Math & OCR Tribal Knowledge   |
+-----------------------------------------+-----------------------------------------+
                                          |
        +---------------------------------+---------------------------------+
        |                                 |                                 |
        v                                 v                                 v
+-----------------------+       +-----------------------+       +-----------------------+
| Phase 1 & 2 (Pro)     |       | Phase 3 & 4 (Flash)   |       | Phase 5 & 6 (Pro)     |
| Spec Gate & Contract  | ----> | Strangler Refactor &  | ----> | OWASP Security &      |
| Boundary Locking      |       | Adversarial Vitest    |       | Architecture Audit    |
+-----------------------+       +-----------------------+       +-----------------------+
```

---

### Phase 1: Specification & Ambiguity Gate (`01-spec-and-ambiguity-gate`)

**Objective:** Perform read-only reconnaissance, map module boundaries, and extract non-obvious domain tribal knowledge into `E:\dynamicStats\AGENTS.md` before writing a single line of code.

#### 1. Reconnaissance Findings
- Mapped the monorepo split between `backend/` (Express 5 REST API, `pg` connection pool, Multer memory uploads, `@google/genai` `gemini-2.5-flash` vision parser) and `frontend/` (Vue 3 SPA, Pinia store, sabermetric calculation engine).
- Identified `Restricted / Human-Approval Paths` in `AGENTS.md`: `render.yaml`, destructive maintenance scripts (`backend/src/scripts/delete_*.ts`), and workspace manifests.

#### 2. Domain Tribal Knowledge Extracted into `AGENTS.md` (What LLMs Cannot Guess)
Without explicit domain rules in `AGENTS.md`, generic coding agents routinely corrupt baseball statistics by treating Innings Pitched (`IP`) as standard base-10 decimals or applying 9-inning MLB formulas to youth games:
1. **Base-3 Innings Pitched (`IP`) Notation**: Baseball records partial innings in base-3 notation where `.1` = $\frac{1}{3}$ inning (1 out) and `.2` = $\frac{2}{3}$ inning (2 outs). Adding `1.1 + 1.2` in base-10 yields `2.3` (invalid), whereas baseball arithmetic requires `parseIP(1.1) + parseIP(1.2) = 1.333... + 1.666... = 3.0` (`"3.0"` via `formatIP()`).
2. **6-Inning Youth Baseball `ERA` & `K/6`**: Youth baseball games are 6 innings, not 9. Earned Run Average and Strikeouts per Game must be scaled by `6`:
   $$\text{ERA} = \frac{\text{ER} \times 6}{\text{IP}_{\text{num}}}, \quad \text{K/6} = \frac{\text{SO} \times 6}{\text{IP}_{\text{num}}}$$
3. **Dynamic Total Bases (`TB`)**: Box scores frequently omit or miscalculate Total Bases in OCR screenshots; `calculateBattingStats` must compute `TB` dynamically from component hits:
   $$\text{TB} = \text{H} + \text{2B} + (2 \times \text{3B}) + (3 \times \text{HR})$$
4. **Gemini Vision OCR Trailing-Dot Player Resolution**: Mobile box-score screenshots truncate long player names with ellipses (`"E. Swant..."`). During `/api/ingest/confirm`, player resolution must strip trailing periods (`/\.+$/`), trim whitespace, and resolve against the active roster in strict priority order:
   - **(1) Exact Match**: Case-insensitive match on cleaned name.
   - **(2) Prefix Match (`>= 3` chars)**: Case-insensitive `startsWith` match when the truncated OCR token has at least 3 characters (e.g., `"e swant"` matches `"E Swantee"`, while short fragments like `"Jo."` are blocked from false-positive prefix matching).
   - **(3) Jersey Number Match (`> 0`)**: Fallback match by uniform number if `Number(line.number) > 0`.
   - **(4) Transactional New Player Creation & Number Backfill**: If unmatched, create a new UUID player inside the active PostgreSQL transaction and append to the in-memory roster array so subsequent batting/pitching lines in the same game reuse the same `playerId`. If matched on name where `existingPlayer.number === 0` and the OCR line supplies a valid jersey number, backfill `player.number` inside the transaction.

---

### Phase 2 & Phase 3: Contract-First Boundaries & Brownfield Safe Refactor (`02` + `03`)

**Objective:** Lock shared TypeScript contracts first, assign disjoint file-ownership packets to subagents, and apply the **Strangler Fig Pattern** to isolate untestable legacy code without breaking existing REST API behavior.

#### 1. Shared Contract Lock (`Phase 2`)
- Locked `backend/src/types/index.ts` and `frontend/src/types/index.ts` (`Player`, `Game`, `BattingLine`, `PitchingLine`).
- Assigned disjoint write boundaries:
  - **Backend Subagent Packet**: `backend/src/playerResolver.ts`, `backend/src/index.ts`, `backend/src/db/init.ts`, `backend/src/__tests__/`.
  - **Frontend Subagent Packet**: `frontend/src/utils/statsEngine.ts`, `frontend/src/__tests__/`.

#### 2. Strangler Fig Extraction: `backend/src/playerResolver.ts` (`Phase 3`)
- **Problem**: In `backend/src/index.ts` (lines 67–99), `resolvePlayer` was embedded directly inside the `POST /api/ingest/confirm` route handler, tightly coupled to `crypto.randomUUID()` and `savePlayerTx(client, ...)`.
- **Refactor**: Extracted the OCR resolution algorithm into `backend/src/playerResolver.ts` with dependency-injected persistence (`savePlayerFn`) and ID generation while keeping `POST /api/ingest/confirm` in `backend/src/index.ts` as a thin orchestrator delegating to `playerResolver`.
- **Behavioral Invariant Preserved**: The external REST contract (`POST /api/ingest/confirm` request/response payload) and `saveGameData` transactional contract in `backend/src/store.ts` remained 100% backward-compatible.

#### 3. Sabermetric Engine Hardening: `frontend/src/utils/statsEngine.ts` (`Phase 3`)
- **Problem**: While `calculateBattingStats` defensively coerced every field via `Number(line.X) || 0`, `calculatePitchingStats` performed raw uncoerced addition (`acc.H += line.H`, `acc.pitches += line.pitches`, etc.) and passed `line.IP` directly to `parseIP`. When Gemini Vision OCR or form edits produced string numbers (e.g., `"1.2"` or `"14"`) or omitted optional pitching stats (`HBP`, `pitches`, `strikes`, `BF`, `WP`), raw addition risked string concatenation or `NaN` propagation across `ERA`, `WHIP`, `K6`, and `strikePct`.
- **Refactor**: Hardened `parseIP` and `calculatePitchingStats` in `frontend/src/utils/statsEngine.ts` with explicit `Number(...) || 0` coercion and negative/NaN guards.

#### 4. PostgreSQL Schema Defect Fix: `backend/src/db/init.ts` (`Phase 3`)
- **Problem**: `backend/src/db/init.ts` (line 13) defined `positions VARCHAR[] NOT NULL` on the `players` table without a `DEFAULT`, whereas `savePlayerTx` in `backend/src/store.ts` executes `INSERT INTO players (id, name, number) VALUES ($1, $2, $3)`. Any new player created on a freshly initialized database would trigger a PostgreSQL `23502 not_null_violation` error and roll back the entire game ingestion transaction.
- **Fix**: Updated `backend/src/db/init.ts` to `positions VARCHAR[] NOT NULL DEFAULT '{}'`, aligning the DDL with `store.ts`.

---

### Phase 4: Adversarial Test Gate (`04-adversarial-test-gate`)

**Objective:** Replace `echo "Error: no test specified" && exit 1` with deterministic, fast-running Vitest test suites covering happy paths, boundary conditions, malformed OCR inputs, and PostgreSQL transaction rollback semantics.

#### Deterministic Verification Matrix Added

| Workspace | Test Suite / Gate | Adversarial Scenarios Verified |
| :--- | :--- | :--- |
| **Backend (`backend/`)** | `src/__tests__/playerResolver.test.ts` | 1. Case-insensitive exact match (`"n shah"` -> `"N Shah"`)<br>2. Trailing-dot OCR truncation + `>= 3` char prefix match (`"E. Swant..."` -> `"E Swantee"`)<br>3. Short-prefix safety guard (`< 3` chars e.g. `"Jo..."` does **not** falsely prefix-match `"John"` and instead creates or matches by number)<br>4. Jersey number fallback (`number: 27` when OCR name is garbled)<br>5. Jersey number `0` avoided as wildcard match<br>6. Backfilling jersey number onto an existing player whose stored `number === 0`<br>7. New player UUID creation + in-memory roster mutation so a player batting and pitching in the same game resolves to a single `playerId` |
| **Backend (`backend/`)** | `src/__tests__/store.test.ts` | 1. Full transactional lifecycle (`BEGIN` -> `resolvePlayer` -> `saveGameTx` -> `DELETE`/`INSERT` batting & pitching lines -> `COMMIT` -> `client.release()`)<br>2. **Adversarial Mid-Transaction Failure**: Simulates a database error during stat-line insertion and verifies `ROLLBACK` is executed and `client.release()` is guaranteed in `finally` |
| **Frontend (`frontend/`)** | `src/__tests__/statsEngine.test.ts` | 1. Base-3 `parseIP` / `formatIP` round-trips (`0.1 + 0.2 = 1.0`, `1.2 + 2.2 = 4.1`)<br>2. Youth 6-inning `ERA` (`(ER * 6) / IP_num`) and `K/6` accuracy<br>3. Dynamic `TB` calculation (`H + 2B + 2*3B + 3*HR`) ignoring stale OCR `TB` values<br>4. Zero-division guards (`0 AB`, `0 PA`, `0.0 IP`, `0 pitches`) returning `"0.00"` / `".000"` / `"0.0%"` without `NaN` or `Infinity`<br>5. Adversarial string/null/undefined coercion across both batting and pitching lines |

---

### Phase 5 & Phase 6: Pre-PR Security & Architecture Audit (`06-pre-pr-security-review`)

Following `.agents/skills/06-pre-pr-security-review/SKILL.md`, we audited the codebase and changeset against **OWASP Top 10**, **OWASP LLM Top 10**, **Supply-Chain Anti-Slopsquatting**, and **Database Concurrency/Transaction Boundaries**.

#### 1. 🟢 `[VERIFIED]` — Passing Gates & Architectural Strengths
- **SQL Injection Immunity (`OWASP A03`)**: Every query in `backend/src/store.ts` (`savePlayerTx`, `saveGameTx`, `saveBattingLinesTx`, `savePitchingLinesTx`, `deleteGame`) uses strict parameterized `$1..$15` placeholders via `pg`. Zero string-concatenated SQL exists.
- **Transactional Atomicity (`ACID`)**: `saveGameData` (`backend/src/store.ts:106-146`) acquires a dedicated `PoolClient`, wraps player resolution, game upsert (`ON CONFLICT (id) DO UPDATE`), and stat-line replacement in a single `BEGIN ... COMMIT` block, executes `ROLLBACK` on any exception, and guarantees `client.release()` inside `finally`.
- **External AI Call Outside DB Transaction**: `POST /api/ingest/screenshot` calls Gemini Vision (`parseBoxScore`) in a separate stateless request prior to `POST /api/ingest/confirm`. No PostgreSQL connection or transaction lock is held open across the external Google Gemini HTTP call.
- **Zero Hardcoded Secrets**: `GEMINI_API_KEY` and `DATABASE_URL` are loaded strictly from `process.env` (`backend/src/geminiService.ts:3`, `render.yaml:12-15`).
- **Supply-Chain Hygiene**: Test tooling (`vitest`) uses established, standard ecosystem packages; all deterministic build, typecheck, and test gates defined in `AGENTS.md` pass with exit code `0`.

#### 2. 🛠️ `[FIXED IN THIS EXERCISE]` — Brownfield Defects Resolved
| ID | File | Defect Class | Resolution Applied |
| :--- | :--- | :--- | :--- |
| **FIX-01** | `backend/src/db/init.ts:13` | Schema / Runtime Crash (`23502`) | Added `DEFAULT '{}'` to `positions VARCHAR[] NOT NULL` so `savePlayerTx` succeeds when inserting newly resolved players. |
| **FIX-02** | `backend/src/playerResolver.ts` & `backend/src/index.ts` | Untestable Controller Coupling | Extracted inline OCR player-matching closure via Strangler Fig pattern into a pure unit-tested module. |
| **FIX-03** | `frontend/src/utils/statsEngine.ts` | Uncoerced Numeric Aggregation | Added `Number(...) \|\| 0` coercion to `calculatePitchingStats` and `parseIP` to prevent string concatenation/`NaN` from OCR payloads. |
| **FIX-04** | `backend/package.json` & `frontend/package.json` | Missing Deterministic Test Gates | Wired `vitest run` and `tsc --noEmit` into workspace scripts matching `AGENTS.md` Section 2. |

#### 3. 🟡 `[PHASE 6 BACKLOG WARNINGS]` — Production Hardening Roadmap for `CODEOWNERS`
Because `03-brownfield-safe-refactor` enforces minimal blast radius (`<400 LOC` per PR) and avoids unrequested breaking API changes, the following pre-existing architectural and security findings in `E:\dynamicStats` are logged as **Phase 6 Backlog Warnings** for the next sprint:

| Severity | Category | Location | Finding & Production Remediation Recommendation |
| :--- | :--- | :--- | :--- |
| 🟡 **`[WARNING-01]`** | **DoS / Unbounded Memory Upload (`OWASP A04`)** | `backend/src/index.ts:12,34` | **Finding:** `const upload = multer({ storage: multer.memoryStorage() })` has no `limits.fileSize` cap or `fileFilter` MIME validation. A malicious or accidental upload of a 500MB file to `/api/ingest/screenshot` buffers the entire payload in Node RAM and Base64-encodes it (`+33%` memory overhead), risking an immediate V8 Out-Of-Memory crash.<br>**Remediation:** Configure `limits: { fileSize: 10 * 1024 * 1024 }` (10 MB) and restrict `req.file.mimetype` to `['image/png', 'image/jpeg', 'image/webp']`. |
| 🟡 **`[WARNING-02]`** | **Indirect Prompt Injection & Output Validation (`OWASP LLM01 / LLM02`)** | `backend/src/geminiService.ts:82-122` | **Finding:** `parseBoxScore` sends user-uploaded images to `gemini-2.5-flash` and returns `JSON.parse(response.text)` directly without server-side runtime schema validation (e.g., Zod bounds checking on scores/stats) or prompt-injection delimiters. An adversarial image containing embedded text instructions could manipulate extracted player names or inject extreme numeric values.<br>**Remediation:** Wrap Gemini instructions with explicit `<untrusted_image_ocr>` system boundaries, enforce character allowlists/length caps (`<= 60` chars) on extracted `name` / `opponent` strings, and validate numeric bounds (`0 <= AB <= 20`, `0 <= IP <= 15`) via Zod before returning or saving. |
| 🟡 **`[WARNING-03]`** | **Missing AuthN/AuthZ, Rate Limiting & Wildcard CORS (`OWASP A01 / A05`)** | `backend/src/index.ts:14,34,57,116` | **Finding:** `app.use(cors())` permits any origin, and state-mutating or cost-incurring endpoints (`POST /api/ingest/screenshot`, `POST /api/ingest/confirm`, `DELETE /api/games/:id`) have no authentication middleware or rate limiting. Any external caller who discovers the Render URL can delete games or exhaust the `GEMINI_API_KEY` quota.<br>**Remediation:** Restrict `cors({ origin: process.env.ALLOWED_ORIGIN })`, add `express-rate-limit` (e.g., `10 req/min` on `/api/ingest/screenshot`), and require a coach/admin bearer token or session cookie on `POST`/`DELETE` routes. |
| 🟡 **`[WARNING-04]`** | **Query Scalability (`N+1` Inserts & Unbounded Reads)** | `backend/src/store.ts:15-26,62-94` | **Finding:** `saveBattingLinesTx` and `savePitchingLinesTx` execute sequential `INSERT` statements inside a `for` loop (`O(N)` round-trips inside an open transaction), and `getBattingLines()` / `getPitchingLines()` run unbounded `SELECT *` queries across all seasons.<br>**Remediation:** Batch stat-line inserts using multi-row parameterized `VALUES` or `UNNEST`, and add season/pagination filters (`LIMIT / OFFSET` or `WHERE "gameId" = ...`) as multi-season data grows. |

---

## 3. Director Presentation Takeaway: Why the 2-Layer Enterprise Harness Works in `< 15 Minutes`

Most enterprise AI initiatives fail on brownfield repositories for one of two reasons:
1. **Prompt Bloat (Monolithic Rules Files):** Teams stuff 3,000+ lines of generic coding advice into a single prompt file, causing context rot and diluting critical domain constraints.
2. **Bespoke Per-Repo Prompt Engineering:** Teams spend weeks hand-crafting custom agent workflows for every microservice, which immediately drift out of date.

### What This `dynamicStats` Exercise Proves
Our **2-Layer Enterprise Harness** solves both problems simultaneously:
1. **Layer 1 is 100% Portable (`.agents/skills/01..07`):** The exact same 7 markdown skills designed for a 200-developer Java/TypeScript enterprise organization dropped directly into `E:\dynamicStats` with **zero modifications**. The universal skills already knew *how* to enforce Phase 1 ambiguity gates, Phase 2 contract locking, Phase 3 Strangler Fig extraction, Phase 4 adversarial testing, and Phase 6 OWASP/LLM security reviews.
2. **Layer 2 Takes `< 5 Minutes` to Author (`AGENTS.md`):** To bind those universal skills to a legacy codebase, an engineer only writes a **36-line `AGENTS.md`** specifying three things:
   - **Exact CLI Verification Commands** (`npm test`, `npm run typecheck`, `npm run build`)
   - **Module Ownership & Restricted File Boundaries**
   - **High-Leverage Domain Tribal Knowledge** (Base-3 `.1`/`.2` innings math, 6-inning youth `ERA`, and OCR trailing-dot player resolution)
3. **Immediate Measurable ROI:** Within **15 minutes**, a brownfield repository with `0%` test coverage was transformed into a type-checked, transactionally tested, Strangler-refactored, and security-audited codebase ready for human `CODEOWNERS` review.
