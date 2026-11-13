---
name: 05-db-expand-and-contract
description: Use when creating or modifying SQL migrations, database schemas, ORM entities, or table columns/indexes before writing migration files.
metadata:
  category: discipline
  triggers:
    - database migration
    - alter table
    - rename column
    - drop column
    - flyway liquibase prisma
    - schema evolution
---

# Zero-Downtime Database Migration Skill (Expand & Contract)

## Iron Law
**Never bundle a destructive or locking SQL schema change (`DROP`, `RENAME`, or un-defaulted `NOT NULL`) with an application release. Violating the letter of the rules IS violating the spirit of the rules.**

## 1. The 3-Release Expand-Migrate-Contract Protocol
Container rollbacks take `<60 seconds`, so every migration MUST be **100% backward-compatible with running `N-1` production pods**:
1. **Release 1 — EXPAND (Additive Only)**:
   - Add new columns as `NULLABLE` or with a non-locking `DEFAULT` (e.g., `DEFAULT '{}'` / `DEFAULT 0`).
   - Create indexes with `CREATE INDEX CONCURRENTLY` (PostgreSQL) or `ALGORITHM=INPLACE, LOCK=NONE` (MySQL).
   - Dual-write to old and new columns in application code; keep reading from old column.
2. **Release 2 — MIGRATE & SWITCH**:
   - Backfill historical rows in batched background jobs (`<= 1,000 rows/batch`).
   - Switch application reads to the new column and stop writing to the old column.
3. **Release 3 — CONTRACT (Cleanup After `>= 7-Day` Bake)**:
   - Only after Release 2 runs in production for `>= 7 days` with zero rollbacks, submit a standalone migration to drop the old column.

## 2. Deterministic Migration Gate
- Run the repo's **DB Migration Lint** (`squawk` / `atlas migrate lint` / `flyway:validate`) and verify `migrate up -> migrate down -> migrate up` against a local/Testcontainer DB.
- Halt via `ask_question` before modifying any file under `db/migrations/` (Restricted Path).

## Common Rationalizations
| Excuse | Reality |
| :--- | :--- |
| "Just `RENAME COLUMN` and deploy app together." | During rolling deploy or `<60s` rollback, `N-1` pods crash with `column does not exist`. |
| "Small table, `CREATE INDEX` won't lock long." | Metadata locks queue behind long queries and stall the entire connection pool. |
| "CTO wants old column dropped in this PR." | Dropping a column in the same release makes container rollback impossible. |
| "Dev DB is empty, `NOT NULL` works fine." | It fails instantly on populated staging/production tables. Always add a safe `DEFAULT` or nullable expand. |

## Red Flags - STOP
- Writing `DROP COLUMN`, `DROP TABLE`, `RENAME COLUMN`, or `RENAME TABLE` in a feature PR.
- Adding a `NOT NULL` column without a `DEFAULT` on an existing table.
- Creating an index on a production table without `CONCURRENTLY` / `LOCK=NONE`.
- Modifying an already-committed historical migration file instead of adding a new one.

**All of these mean: STOP immediately. Delete any unapproved edits or weakened tests. Start over.**

## Valid Exceptions (When NOT to Use)
- Brand-new tables created in the current release that have zero existing production rows or `N-1` readers.
- Ephemeral local test fixtures or in-memory mock stores.

## Baton-Pass Chaining (REQUIRED NEXT SKILL)
- After locking the Expand migration, proceed to `02-contract-first-subagents` (if building feature logic) or `04-adversarial-test-gate` (to test dual-write/rollback compatibility).
