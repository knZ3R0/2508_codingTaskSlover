# Risk Answers

A1) Endpoint Ambiguity (search vs base listing)
- Answer: Use the base endpoint https://jsonmock.hackerrank.com/api/asteroids?page={n} to enumerate all records. The repository doc docs/asteroids_v2.txt states 21 pages with consistent structure across pages. /search is not required to obtain the full dataset.
- Impact: BLOCKER resolved. Downgrade related pagination risks accordingly.

A2) JSON Parsing Without External Libraries
- Answer: Implement minimal regex-based parsing for required fields only (total_pages and data[].designation, discovery_date, period_yr, pha). Avoids extra dependencies and respects “No additional classes required.”
- Impact: Remains HIGH but mitigated with targeted parsing and defensive defaults.

A3) Discovery Date Format Variations
- Answer: docs/asteroids_v2.txt shows discovery_date is consistently "YYYY-MM-DD". Extract the first 4-digit year. Defensive fallback: if parsing fails, skip the record.
- Impact: Downgrade to MEDIUM.

A4) period_yr Missing/Non-numeric
- Answer: Dataset shows all numeric strings present; however, per task, if missing or non-numeric → treat as 0.
- Impact: Downgrade to MEDIUM with defaulting.

A5) PHA Semantics and Casing
- Answer: Dataset shows only "Y" or "N" (no "n/a"), but code will still exclude non-Y/N values and compare case-insensitively.
- Impact: MEDIUM retained (minor).

A6) Pagination Integrity
- Answer: Use total_pages from page 1 (docs say 21). Iterate 1..total_pages. If total_pages missing, return empty safely.
- Impact: Downgrade to LOW.

A7) Network/IO Failures
- Answer: Catch exceptions and return an empty list to avoid crashes (Hackerrank style resilience).
- Impact: HIGH retained.

A8) Sorting Stability and Tie-breakers
- Answer: Sort by numeric period_yr asc (default 0) then designation asc (String.compareTo).
- Impact: MEDIUM retained.

A9) Timebox Overrun
- Answer: Keep logic compact (regex extraction for a few fields, single pass pagination). No extra classes or libs.
- Impact: MEDIUM retained.

A10) “No Additional Classes”
- Answer: Implement in single class with minimal private helper methods only (no extra top-level classes).
- Impact: MEDIUM retained.

## Provenance
- Inputs: docs/task.md, docs/goal.md, docs/risks.md, docs/asteroids_v2.txt
- Assumptions: Base endpoint pagination is canonical; still handle missing/non-numeric period defensively; pha filter strict Y/N (case-insensitive).
- Checks: Resolved BLOCKER via repo-provided dataset analysis; adjusted priorities.
