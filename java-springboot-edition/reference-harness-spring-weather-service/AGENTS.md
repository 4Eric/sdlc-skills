# AGENTS.md — Enterprise AI Coding Harness (Java 21 / Spring Boot 3.x)

> **Gartner Level 3–4 Reference Implementation:** Repository-level **AI Coding Harness** for `spring-weather-service`. Every AI coding agent operating in this repository must obey these architectural boundaries, tiered subagent routing policies, and deterministic Maven verification gates.

---

## 1. Service Context & Technology Stack
- **Service**: `spring-weather-service` (Enterprise Weather & Regional Geocoding Microservice)
- **Runtime & Framework**: Java 21 LTS (`spring.threads.virtual.enabled=true`), Spring Boot 3.3.x, Spring `RestClient`, Resilience4j, Spring Data JPA (Hibernate 6), Flyway, Micrometer + Prometheus Actuator
- **Quality & Verification Toolchain**: Maven Wrapper (`./mvnw`), JUnit 5, AssertJ, Mockito, Spring Slice Tests (`@WebMvcTest`, `@DataJpaTest`), Testcontainers (`@ServiceConnection`), ArchUnit, Spotless, Checkstyle, SpotBugs, JaCoCo (`diff-cover`)

### Repository Package Topology
```text
spring-weather-service/
├── AGENTS.md                                      # Repository AI Coding Harness (this file)
├── .agents/skills/spring-feature-harness/SKILL.md # Minimalist <100-line domain skill
├── pom.xml                                        # Locked in Phase 3 (PreToolUse write-lock)
├── src/main/resources/
│   ├── openapi/weather-api.yaml                   # Locked Phase 2 OpenAPI 3.1 Contract
│   └── db/migration/V*__*.sql                     # Flyway Expand-and-Contract SQL migrations
├── src/main/java/com/enterprise/weather/
│   ├── dto/                                       # Locked Phase 2 Java `record` DTOs & Validation
│   ├── client/                                    # Spring RestClient adapters & Photon fallback
│   ├── repository/                                # Spring Data JPA repositories & entities
│   ├── service/                                   # Domain orchestration & transaction boundaries
│   ├── controller/                                # REST controllers & RFC 7807 ProblemDetail advice
│   └── exception/                                 # GlobalExceptionHandler (@RestControllerAdvice)
└── src/test/java/com/enterprise/weather/
    ├── architecture/ArchitectureContractTest.java # Locked Phase 2 ArchUnit boundary rules
    └── ...                                        # Slice, Testcontainers & Golden Master suites
```

---

## 2. Multi-Model Subagent Routing & Disjoint File Ownership
| Subagent Role | Model Tier | Allowed Write Scope | Core Responsibilities |
| :--- | :--- | :--- | :--- |
| **Codebase Researcher (`research`)** | `flash_lite` | *Read-Only (`[]`)* | Fast AST symbol lookup, bean dependency scan, and DTO/OpenAPI inspection |
| **Lead Architect / Contract Planner** | `pro` | `PLAN.md`, `dto/**`, `openapi/**`, `db/migration/**`, `architecture/**` | Phase 1 Spec & Phase 2 Contract Lock (Java `record` DTOs, OpenAPI 3.1, ArchUnit, Flyway) |
| **Client & Persistence Engineer** | `flash` | `src/main/java/.../client/**`, `src/main/java/.../repository/**` | `RestClient` integrations, Resilience4j fallbacks, and N+1-safe JPA queries |
| **Domain Service & Web Engineer** | `pro` | `src/main/java/.../service/**`, `controller/**`, `exception/**` | Business orchestration, virtual-thread safety, and RFC 7807 `ProblemDetail` handlers |
| **Adversarial Test Engineer** | `flash` | `src/test/java/**` *(excl. `architecture/**`)* | JUnit 5, AssertJ, `@WebMvcTest`, `@DataJpaTest`, and singleton Testcontainers |

---

## 3. Tribal Domain Invariants & Spring Boot Engineering Standards
1. **Contract-First Synchronization**: Always update and compile Java `record` DTOs (`dto/`), `weather-api.yaml`, and `ArchitectureContractTest.java` in Phase 2 *before* spawning parallel Phase 3 subagents.
2. **Geocoding Tribal Quirk (Open-Meteo -> Photon Fallback)**:
   - Open-Meteo's geocoding endpoint (`https://geocoding-api.open-meteo.com/v1/search`) returns empty (`[]`) on multi-word regional/borough queries such as `"North York Ontario"`.
   - `WeatherGeocodingClient` must trigger a secondary fallback via Spring `RestClient` (and `@CircuitBreaker(name = "openMeteoGeocoding", fallbackMethod = "photonFallback")`) to OpenStreetMap Photon (`https://photon.komoot.io/api/`), normalizing `admin1` (`Name, Admin1, Country`) and deduplicating coordinates within `0.08` degrees.
3. **Spring Transaction & Concurrency Hygiene**:
   - Never annotate `private` methods with `@Transactional`, and **never** invoke remote `RestClient` HTTP calls inside an active `@Transactional` database context (prevents HikariCP connection pool starvation).
   - Keep virtual threads unpinned: never use `synchronized` blocks around blocking I/O; use `ReentrantLock` if synchronization is required.
4. **RFC 7807 Error Contract & Testcontainers Reuse**:
   - All REST exceptions must map to standard **RFC 7807 `ProblemDetail`** payloads via `@RestControllerAdvice`.
   - Integration tests must inherit from `AbstractIntegrationTest` (reusable singleton `@ServiceConnection` PostgreSQL/Kafka container)—never instantiate per-test containers.

---

## 4. Mandatory Deterministic Verification Gate (Must Exit `0`)
```powershell
# 1. Phase 3 Compile, Formatting, Checkstyle & SpotBugs Static Hygiene Gate
./mvnw clean test-compile spotless:check checkstyle:check spotbugs:check

# 2. Phase 4 Full Unit, Slice, ArchUnit & Testcontainers Suite + JaCoCo Verification
./mvnw verify -Dspotless.check.skip=false
```

---

## 5. Hard Guardrails & Escalation Boundaries
- ✅ **Autonomous within Sandbox**: Implementing scoped beans, adding slice/Testcontainers tests, and fixing Checkstyle/SpotBugs/ArchUnit violations.
- ⛔ **Human Sign-Off Required (Halt Immediately)**: Modifying `pom.xml` dependencies, altering `SecurityFilterChain` / OAuth2 configs, writing destructive Flyway DDL (`DROP`/`RENAME`), or adding `@Disabled` / weakening AssertJ `assertThat(...)` assertions.
