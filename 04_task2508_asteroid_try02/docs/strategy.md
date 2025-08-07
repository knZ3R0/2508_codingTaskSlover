# Strategy

## CORE (80/20)
- Fetch page 1 from base endpoint to read total_pages.
- Iterate pages 1..total_pages; GET JSON each page.
- Minimal JSON parsing (regex/string ops) to extract data[].designation, discovery_date, period_yr, pha.
- Filter: year == first 4 digits of discovery_date; pha equals input (case-insensitive), ignore non-Y/N.
- Default period_yr to 0 on missing/non-numeric.
- Sort by numeric period_yr asc, then designation asc.
- Return list of designations.

## DEFER
- Robust JSON parser or external libs.
- Locale-aware collation for designation.
- Retry/backoff for transient HTTP failures.
- CLI/interactive I/O beyond simple args in main.

## AVOID
- Creating additional classes or adding dependencies.
- Over-general parsing of unused fields.
- Over-engineering pagination (stick to 1..total_pages).

## Patterns (justified)
- Strategy-lite: two-pass approach (page-1 to get total_pages, then fixed loop).
- Comparator for composite sort (period then designation).

## Time Allocation (45 minutes total)
- Spec read and plan (P2–P5 recap): 7 min
- Implementation (HTTP, parsing, filtering, sorting): 24 min
- Verification (manual reasoning, compile checks): 8 min
- Polish (edge-case guards, comments, RELEASE_NOTES): 6 min
- Buffer (~13% included across buckets)
Sum: 45 minutes

## Integrations from risksAnswers
- Use base endpoint; search not required.
- Discovery date always "YYYY-MM-DD"; still defensive extract of year.
- period_yr default 0; handle non-numerics.
- Network failures → return empty list gracefully.

## Plan Check
1. aligns_with_goal yes
2. respects_timebox yes
3. unresolved_blockers 0

## Provenance
- Inputs: docs/task.md, docs/goal.md, docs/risks.md, docs/risksAnswers.md, docs/asteroids_v2.txt
- Assumptions: Base endpoint is canonical; discovery_date starts with year; period_yr numeric string but default to 0 if not; pha is "Y"/"N".
- Checks: Ensured single-class approach with Java 11 HttpClient and minimal parsing; tie-breakers per task.
