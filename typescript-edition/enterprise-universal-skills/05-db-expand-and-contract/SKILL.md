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

## 1. The Stateful Rollback Problem
Container rollbacks take `<60 seconds`, but destructive database schema changes are irreversible. Every migration bundled with an application release MUST be **100% backward-compatible with the currently running `N-1` production version**.

## 2. The 3-Release Expand-and-Contract Protocol
Never rename or drop a column/table in a single release. Split schema evolution across 3 separate deployments:

1. **Release 1 — EXPAND (Additive Only)**:
   - Add the new column as `NULLABLE` (or with a non-locking `DEFAULT`, e.g., `DEFAULT '{}'` / `DEFAULT 0`).
   - Create indexes using `CREATE INDEX CONCURRENTLY` (PostgreSQL) or `ALGORITHM=INPLACE, LOCK=NONE` (MySQL) to prevent table locks.
   - Update application code to **dual-write** to both old and new columns while reading from the old column.
2. **Release 2 — MIGRATE & SWITCH**:
   - Backfill historical rows in batched background transactions (`<= 1,000 rows/batch`).
   - Switch application reads to the new column and stop writing to the old column.
3. **Release 3 — CONTRACT (Cleanup Only After Bake Period)**:
   - Only after Release 2 has baked in production for `>= 7 days` with zero rollbacks, submit a standalone cleanup migration to drop the deprecated column.

## 3. Hard Red Lines & Deterministic Migration Gate (Blocked by CI `squawk` / `atlas migrate lint`)
- ⛔ **PROHIBITED in App Releases**: `DROP COLUMN`, `DROP TABLE`, `RENAME COLUMN`, `RENAME TABLE`, or adding `NOT NULL` without a default on existing tables.
- ⛔ **Mandatory Reversibility Check**: Every migration must pass the repo's **DB Migration Lint** (`squawk` / `atlas migrate lint` / `flyway:validate`) and an automated `migrate up -> migrate down -> migrate up` verification against a local/Testcontainer DB before PR merge.
- ⛔ **Restricted Path Gate**: Halt via `ask_question` before modifying any file under `db/migrations/`.

## 4. Common Rationalizations
| Excuse | Reality |
| :--- | :--- |
| "Just `RENAME COLUMN` and deploy app together." | During rolling deploy or `<60s` rollback, `N-1` pods crash with `column does not exist`. |
| "Small table, `CREATE INDEX` won't lock long." | Metadata locks queue behind long queries and stall the entire connection pool. |
| "CTO wants old column dropped in this PR." | Dropping a column in the same release makes container rollback impossible. |
| "Dev DB is empty, `NOT NULL` works fine." | It fails instantly on populated staging/production tables. Always add a safe `DEFAULT` or nullable expand. |

## 5. Red Flags - STOP
- Writing `DROP COLUMN`, `DROP TABLE`, `RENAME COLUMN`, or `RENAME TABLE` in a feature PR.
- Adding a `NOT NULL` column without a `DEFAULT` on an existing table.
- Creating an index on a production table without `CONCURRENTLY` / `LOCK=NONE`.
- Modifying an already-committed historical migration file instead of adding a new one.

**All of these mean: STOP immediately. Delete any unapproved edits or weakened tests. Start over.**

## 6. Valid Exceptions (When NOT to Use)
- Brand-new tables created in the current release that have zero existing production rows or `N-1` readers.
- Ephemeral local test fixtures or in-memory mock stores.

## 7. Baton-Pass Chaining (REQUIRED NEXT SKILL)
- After locking the Expand migration, proceed to `02-contract-first-subagents` (if building feature logic) or `04-adversarial-test-gate` (to test dual-write/rollback compatibility).
