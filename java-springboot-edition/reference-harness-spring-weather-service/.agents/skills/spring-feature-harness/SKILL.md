---
name: spring-feature-harness
description: Contract-First multi-agent workflow and deterministic Maven/JaCoCo verification gate for spring-weather-service (Java 21 / Spring Boot 3.x).
owner: platform-architecture-team
last_verified: 2026-10-31
---

# Spring Weather Service — Feature Harness Skill

Invoke this skill whenever implementing a REST endpoint, external client integration, database migration, or refactor in `spring-weather-service`. Enforce the 3-step **Contract Lock -> Parallel Disjoint Subagents -> Deterministic Maven Gate** loop.

---

## Step 1: Lock Phase 2 Contracts (Preventing Parallel Subagent Drift)
Before spawning implementation or test subagents, update and verify the Single Source of Truth contracts:
1. **Java `record` DTOs & Bean Validation**: Define immutable `record` payloads with `@NotBlank`/`@NotNull`/`@Valid` in `src/main/java/com/enterprise/weather/dto/` (`WeatherResponseRecord`, `DailyForecastRecord`, `GeocodingResultRecord`).
2. **OpenAPI 3.1 & Flyway Expand-Only SQL**: Update `src/main/resources/openapi/weather-api.yaml` and any additive-only Flyway migration in `src/main/resources/db/migration/V*__*.sql` (no `DROP`/`RENAME`).
3. **ArchUnit Boundary Lock**: Ensure `ArchitectureContractTest.java` locks `controller -> service -> (client | repository)` package rules.
4. **Verify Contract Compilation**: Run `./mvnw test-compile -Dtest=ArchitectureContractTest test` (`exit 0`). Lock `dto/**`, `openapi/**`, and `pom.xml` as read-only for Step 2 workers.

---

## Step 2: Parallel Tiered Subagent Dispatch (Disjoint Write Scopes)
Dispatch parallel subagents matched to the optimal model tier and strictly disjoint directory paths:

1. **Client & Persistence Subagent (`Model: "flash"`, Scope: `client/**`, `repository/**`)**
   - Implement `WeatherGeocodingClient` using Spring Boot 3.x `RestClient` and Resilience4j `@CircuitBreaker(name = "openMeteoGeocoding", fallbackMethod = "photonFallback")`.
   - **Mandatory Tribal Invariant**: When Open-Meteo returns `0` results or the query contains multiple tokens/commas (e.g., `"North York Ontario"`), fallback to OpenStreetMap Photon (`https://photon.komoot.io/api/`), normalize `admin1` (`city + state`), and deduplicate coordinates within `0.08` degrees.
   - Ensure Spring Data JPA queries use `@EntityGraph` / `JOIN FETCH` to prevent Hibernate `N+1` queries.
2. **Domain Service & Controller Subagent (`Model: "pro"`, Scope: `service/**`, `controller/**`, `exception/**`)**
   - Implement virtual-thread-safe business logic (never wrap remote `RestClient` calls inside `@Transactional` methods; avoid `synchronized` blocks).
   - Map all domain and validation failures to **RFC 7807 `ProblemDetail`** responses in `@RestControllerAdvice` (`GlobalExceptionHandler`).
3. **Adversarial Verification & Test Subagent (`Model: "flash"`, Scope: `src/test/java/**` excl. `architecture/**`)**
   - Write JUnit 5 + AssertJ + WireMock/MockRestServiceServer tests, `@WebMvcTest` controller slice tests (verifying RFC 7807 `ProblemDetail`), and `@DataJpaTest` / singleton `@ServiceConnection` Testcontainers suites extending `AbstractIntegrationTest`.
   - **Red Line**: Never add `@Disabled`, delete existing `@Test` methods, or weaken AssertJ `assertThat(...)` assertions.

---

## Step 3: Mandatory Deterministic Verification Gate (Must Exit `0`)
Execute the full Maven verification harness before returning results to the human engineer:

```powershell
./mvnw clean test-compile spotless:check checkstyle:check spotbugs:check
./mvnw verify
```

### Definition of Done Checklist:
- [ ] `spotless:check`, `checkstyle:check`, and `spotbugs:check` exit `0` with `0` warnings.
- [ ] All JUnit 5 unit, slice, ArchUnit, and Testcontainers tests pass (`0` failures, `0` `@Disabled`).
- [ ] JaCoCo coverage (`>= 85%` branch coverage on new/modified lines via `diff-cover`) exits `0`.
- [ ] Self-heal any compiler, ArchUnit, or test failure (max 3 retries) before notifying the engineer.
