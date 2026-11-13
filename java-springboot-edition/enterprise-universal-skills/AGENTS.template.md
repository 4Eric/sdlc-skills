# AGENTS.md — Universal Enterprise Repository Harness Template
# Copy to the root of any repository and fill in the bracketed values (<10 mins).

## 1. Service Identity & Architecture
- **Service Name**: `[e.g., order-fulfillment-service / customer-portal-web]`
- **Domain Owner**: `[e.g., Checkout & Payments Platform Squad]`
- **Tech Stack**: `[e.g., Java 21 + Spring Boot 3.3 + PostgreSQL  OR  TypeScript 5 + React 18 + Node 20]`
- **Architecture Pattern**: `[e.g., Hexagonal / Layered: controller -> service -> repository -> client]`

## 2. Deterministic Verification Commands (Single Source of Truth)
Universal skills execute these exact commands to verify work in this repository:
- **Compile & Typecheck**: `[e.g., ./mvnw test-compile  OR  npm run typecheck]`
- **Format & Static Lint**: `[e.g., ./mvnw spotless:check checkstyle:check  OR  npm run lint]`
- **Unit & Slice Tests**: `[e.g., ./mvnw test  OR  npm test]`
- **Runtime Smoke Check**: `[e.g., ./mvnw spring-boot:run  OR  npm run start (verify CommonJS/ESM compatibility)]`
- **Differential Coverage Gate**: `[e.g., diff-cover target/site/jacoco/jacoco.xml --fail-under=85]`
- **DB Migration Lint**: `[e.g., squawk migrations/*.sql  OR  ./mvnw flyway:validate]`

## 3. Repository Module Boundaries (For Subagent File Locking)
- **API Contracts / DTOs (Lock in Phase 2)**: `[e.g., src/main/java/.../dto/  OR  src/types/]`
- **Domain / Service Layer**: `[e.g., src/main/java/.../service/  OR  src/services/]`
- **API Controllers / UI Views**: `[e.g., src/main/java/.../controller/  OR  src/components/]`
- **Test Suites**: `[e.g., src/test/java/  OR  src/__tests__/]`
- **Restricted / Human-Approval Paths**: `[e.g., src/.../security/, db/migrations/, terraform/, pom.xml, package.json]`

## 4. Proprietary Internal SDKs & Tribal Knowledge (What LLMs Cannot Guess)
- **Authentication**: Always use internal `[e.g., @company/auth-sdk / SecurityContextUtil]`; never parse raw JWTs manually.
- **Error Standard**: Return RFC 7807 `ProblemDetail` payloads using `[e.g., @CompanyExceptionHandler / AppError]`.
- **External HTTP Calls**: Must wrap upstream calls with `[e.g., Resilience4j @CircuitBreaker / company fetchWithRetry]` and explicit timeouts (`<= 3000ms`).
- **Logging & PII**: Include `correlationId` in structured logs; NEVER log `ssn`, `cardNumber`, `password`, or `accessToken`.

## 5. Multi-Model Subagent Routing Policy (Gartner FinOps Governance)
- **`flash_lite`**: Read-only codebase search, symbol lookup, and untrusted ticket/log sanitization.
- **`flash`**: Boilerplate implementation, data mappers, and unit/integration test generation.
- **`pro`**: Phase 2 contract design, complex state/concurrency logic, security review, and incident RCA.

## 6. Natural SDD & Closed-Loop Router
Developer prompts trigger these automatic skill sequences without requiring explicit skill numbers:
- **New Feature / API Integration**: `01-spec-and-ambiguity-gate` -> `02-contract-first-subagents` -> `04-adversarial-test-gate` -> `06-pre-pr-security-review`
- **Legacy / <50% Coverage Refactor**: `03-brownfield-safe-refactor` -> `04-adversarial-test-gate` -> `06-pre-pr-security-review`
- **SQL / Schema Change**: `05-db-expand-and-contract` (paired with `01` / `02` when adding feature logic)
- **Production Crash / 5xx Alert**: `07-incident-rca-and-hotfix` -> `04-adversarial-test-gate` -> `06-pre-pr-security-review`
- **Closed-Loop Verification Cycle**: Every implementation runs `Compile -> Test -> Runtime Smoke Check` with up to **3 automated self-heal retries** before triggering a **Circuit-Breaker Halt (`ask_question`)**.
