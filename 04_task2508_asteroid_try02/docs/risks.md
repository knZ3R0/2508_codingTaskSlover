# Risks

1) Endpoint Ambiguity (search vs base listing): The task mentions both base and search endpoints; unclear which guarantees full dataset enumeration. | If unresolved: incomplete results or incorrect filtering logic.
Priority: BLOCKER
Questions:
1) Should we enumerate all records via base endpoint /api/asteroids?page={n}?
2) Is /search mandatory, and if so what is the “parameter” we must pass to retrieve all?
3) Are there records only visible via /search that are not in base listing?

2) JSON Parsing Without External Libraries: JDK 11 has no built-in JSON parser; minimal manual parsing is required. | If unresolved: brittle parsing, runtime errors on edge cases.
Priority: HIGH

3) Discovery Date Format Variations: Year extraction assumes a 4-digit year present in discovery_date; formats may vary. | If unresolved: valid records might be skipped or mis-filtered.
Priority: HIGH

4) period_yr Missing/Non-numeric: period_yr can be absent or non-numeric despite spec; must default to 0. | If unresolved: NumberFormatException or incorrect sorting.
Priority: HIGH

5) PHA Semantics and Casing: pha may be "Y", "N", or "n/a"; param is "Y"/"N". Case and "n/a" handling must be explicit. | If unresolved: Incorrect inclusion/exclusion of records.
Priority: MEDIUM

6) Pagination Integrity: Relying on total_pages and looping; inconsistent totals could miss pages. | If unresolved: Partial result set.
Priority: MEDIUM

7) Network/IO Failures: Remote API could be unreachable or rate-limited. | If unresolved: Function may throw or return empty results.
Priority: HIGH

8) Sorting Stability and Tie-breakers: Must sort by numeric period_yr asc, then designation asc (locale?). | If unresolved: Unstable ordering and nondeterministic outputs.
Priority: MEDIUM

9) Timebox Overrun: Manual parsing and pagination can exceed 45 minutes if over-engineered. | If unresolved: Incomplete implementation.
Priority: MEDIUM

10) “No Additional Classes” Constraint: Limits decomposition; risk of high method complexity if not carefully structured. | If unresolved: Hard-to-maintain code or violations of constraints.
Priority: MEDIUM

## Edge Cases (≥5)
- E1: period_yr is null, empty, "n/a", or non-numeric → treat as 0.
- E2: discovery_date missing or lacking a 4-digit year; skip safely.
- E3: pha values in lowercase or with whitespace; enforce case-insensitive compare, exclude "n/a".
- E4: data array empty for some pages; continue pagination.
- E5: designation ties with identical names; tie-break by designation still deterministic (string compare).
- E6: total_pages is 0 or missing; handle gracefully (return empty).
- E7: Large page counts; ensure loop and memory remain bounded.
- E8: Unexpected extra fields; parser must ignore unknown keys.
- E9: HTTP non-200 response; handle by stopping and returning what’s safe (or empty) with comments.
- E10: Mixed period_yr formats (integer vs decimal vs quoted).

## Provenance
- Inputs: docs/task.md, docs/goal.md
- Assumptions: Use base endpoint /api/asteroids?page={n} for full enumeration; discovery year is first 4-digit substring of discovery_date; period_yr default 0 on missing/non-numeric; pha filter is exact Y/N (case-insensitive), excluding "n/a".
- Checks: Aligned risks to task constraints (no tests, no extra classes, 45-min timebox); emphasized pagination, parsing, and sorting tie-breakers.
