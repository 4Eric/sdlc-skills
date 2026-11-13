# AGENTS.md — Universal Enterprise Repository Harness Template
# Copy to root of any repository and fill bracketed values (<10 mins).

## 1. Service Identity & Architecture
- **Service Name**: `[e.g., order-fulfillment-service]` | **Domain Owner**: `[e.g., Payments Squad]`
- **Tech Stack**: `[e.g., TypeScript 5 + Node 20 + PostgreSQL  OR  Java 21 + Spring Boot 3.3]`
- **Architecture**: `[e.g., Hexagonal / Layered: controller -> service -> repository -> client]`

## 2. Deterministic Verification Commands (Single Source of Truth)
Universal skills execute these exact commands to verify changes in this repository:
- **Compile & Typecheck**: `[e.g., npm run typecheck  OR  ./mvnw test-compile]`
- **Format & Static Lint**: `[e.g., npm run lint  OR  ./mvnw spotless:check]`
- **Unit & Slice Tests**: `[e.g., npm test  OR  ./mvnw test]`
- **Runtime Smoke Check**: `[e.g., npm run start  OR  ./mvnw spring-boot:run (verify ESM/CJS runtime boot)]`
- **Differential Coverage Gate**: `[e.g., diff-cover coverage.xml --fail-under=85]`
- **DB Migration Lint**: `[e.g., atlas migrate lint  OR  squawk migrations/*.sql]`

## 3. Repository Module Boundaries (For Subagent File Locking)
- **API Contracts / DTOs (Lock in Phase 2)**: `[e.g., src/types/  OR  src/.../dto/]`
- **Domain / Service Layer**: `[e.g., src/services/  OR  src/.../service/]`
- **API Controllers / UI Views**: `[e.g., src/components/  OR  src/.../controller/]`
- **Test Suites**: `[e.g., src/__tests__/  OR  src/test/java/]`
- **Restricted / Human-Approval Paths**: `[e.g., security/, db/migrations/, pom.xml, package.json]`

## 4. Proprietary SDKs & Tribal Knowledge (What LLMs Cannot Guess)
- **Authentication**: Always use internal `[e.g., @company/auth-sdk]`; never parse raw JWTs.
- **Error Standard**: Return RFC 7807 `ProblemDetail` via `[e.g., AppError / @CompanyExceptionHandler]`.
- **External HTTP**: Wrap remote calls with `[e.g., fetchWithRetry / Resilience4j]` (timeout `<= 3000ms`).
- **Logging & PII**: Structured JSON with `correlationId`; NEVER log passwords, cards, SSNs, or tokens.

## 5. Natural SDD & Closed-Loop Router
Developer prompts trigger these automatic skill sequences without explicit numbering:
- **New Feature / API Integration**: `01-spec-and-ambiguity-gate` -> `02-contract-first-subagents` -> `04-adversarial-test-gate` -> `06-pre-pr-security-review`
- **Legacy / <50% Coverage Refactor**: `03-brownfield-safe-refactor` -> `04-adversarial-test-gate` -> `06-pre-pr-security-review`
- **SQL / Schema Change**: `05-db-expand-and-contract` (paired with `01` / `02` when adding feature)
- **Production Crash / 5xx Alert**: `07-incident-rca-and-hotfix` -> `04-adversarial-test-gate` -> `06-pre-pr-security-review`

### Universal Closed-Loop Verification Cycle
All implementations must run: `Compile -> Test -> Runtime Smoke Check`.
- **Auto Self-Heal**: Max 3 retries on deterministic failures.
- **Circuit-Breaker Halt**: If failing after 3 attempts, halt immediately and call `ask_question`.
