# Cautions

## Anti-pattern defenses
1) Over-parsing JSON → Detection: Complex stateful scanning, nested brace tracking. → Prevention/Refactor: Use targeted regex/string extraction for only total_pages and required fields within data array; keep helpers small and single-purpose.
2) Ignoring pagination → Detection: Only requesting page=1. → Prevention: Always parse total_pages from page 1 and iterate 1..total_pages; guard if total_pages missing (treat as 0).
3) Unsafe number parsing → Detection: Direct Double.parseDouble without checks. → Prevention: Validate numeric with regex; default period_yr to 0 on failure; trim quotes/whitespace.
4) Case-sensitive filters → Detection: Comparing pha with equals. → Prevention: Uppercase both input and record; accept only Y/N; exclude others.
5) High cyclomatic complexity in one method → Detection: Long method, nested loops/conditionals. → Prevention: Decompose into small private helpers (HTTP GET, total_pages parse, data extraction, field extraction).
6) Silent HTTP failures → Detection: Uncaught exceptions propagate or partial results misused. → Prevention: Catch IO/Interrupted; return empty list; keep behavior deterministic per task.
7) Unstable sorting → Detection: Only sort by period_yr. → Prevention: Composite comparator by (period_yr asc, designation asc).

## Concurrency model
- No concurrency. Sequential HTTP calls per page (1..total_pages). Dataset is small (~202 items). No shared mutable state across threads → simpler reasoning, deterministic ordering.

## Error handling hierarchy
- Input validation errors: invalid pha (not Y/N) → return empty list.
- Network/HTTP errors: catch exceptions, return empty list (fail-safe).
- Parsing errors (total_pages/data fields): default conservatives (total_pages=0, period_yr=0), skip malformed records.
- Logging: keep minimal inline comments; no logging framework per constraints.

## Observability policy
- Scope-limited; no metrics/tracing. Deterministic output and clear defaults serve as observability proxies. Optional: print from main for manual inspection only.

## Dependency boundaries and MOCKABLE seams
- HTTP boundary: Java 11 HttpClient; isolated via httpGet(url) helper which can be swapped in tests (if ever needed) by method indirection.
- Parsing boundary: parseTotalPages(json), extractDataArray(json), extractField(obj, name) provide seams for focused reasoning.

## Provenance
- Inputs: docs/task.md, docs/goal.md, docs/risks.md, docs/risksAnswers.md, docs/strategy.md, docs/requirements.md, docs/asteroids_v2.txt
- Assumptions: Single-threaded execution; minimal JSON parsing sufficient; defaults on parsing failures; strict Y/N pha.
- Checks: Anti-patterns mapped to known risks; concurrency not required; fail-safe error policy aligns with Hackerrank-style tasks.
