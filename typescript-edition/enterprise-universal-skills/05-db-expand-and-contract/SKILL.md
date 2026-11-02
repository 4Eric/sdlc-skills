---
name: 05-db-expand-and-contract
description: Universal Phase 2, 6 & 7 database migration skill. Enforces Expand-Migrate-Contract SQL rules so <60s container rollbacks never fail against N-1 running pods.
last_verified: 2026-10-30
---

# Zero-Downtime Database Migration Skill (Expand & Contract)

Use this skill whenever creating or reviewing SQL schema migrations (Flyway, Liquibase, Prisma, Drizzle, Alembic, EF Core).

## 1. The Stateful Rollback Problem
Container rollbacks take `<60 seconds`, but destructive database schema changes are irreversible. Every migration bundled with an application release MUST be **100% backward-compatible with the currently running `N-1` production version**.

## 2. The 3-Release Expand-and-Contract Protocol
Never rename or drop a column/table in a single release. Split schema evolution across 3 separate deployments:

1. **Release 1 — EXPAND (Additive Only)**:
   - Add the new column as `NULLABLE` (or with a non-locking `DEFAULT`).
   - Create indexes using `CREATE INDEX CONCURRENTLY` (PostgreSQL) or `ALGORITHM=INPLACE, LOCK=NONE` (MySQL) to prevent table locks.
   - Update application code to **dual-write** to both old and new columns while reading from the old column.
2. **Release 2 — MIGRATE & SWITCH**:
   - Backfill historical rows in batched background transactions (`<= 1,000 rows/batch`).
   - Switch application reads to the new column and stop writing to the old column.
3. **Release 3 — CONTRACT (Cleanup Only After Bake Period)**:
   - Only after Release 2 has baked in production for `>= 7 days` with zero rollbacks, submit a standalone cleanup migration to drop the deprecated column.

## 3. Hard Red Lines (Blocked by CI `squawk` / `atlas migrate lint`)
- ⛔ **PROHIBITED in App Releases**: `DROP COLUMN`, `DROP TABLE`, `RENAME COLUMN`, `RENAME TABLE`, or adding `NOT NULL` without a default on existing tables.
- ⛔ **Mandatory Reversibility Check**: Every migration must pass an automated `migrate up -> migrate down -> migrate up` verification against an ephemeral Testcontainer DB before PR merge.
