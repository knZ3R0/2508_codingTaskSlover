1. goal
Develop asteroidMonitor that must fetch+paginate asteroid data, filter by year and PHA, and sort by period_yr within 45 minutes, specifically handling missing period_yr=0 and pagination through Java 11 HttpClient + minimal JSON parsing. Exclusions: tests, extra classes; assumption: base endpoint pages.

2. rationale
- Java 11 HttpClient fits the repo’s no-extra-deps constraint and enables deterministic HTTP GET requests.
- Minimal in-file JSON parsing avoids adding libraries (aligned with “No additional classes required”) while remaining adequate for this flat schema (top-level totals + data array of simple objects).
- Pagination is addressed by reading total_pages then iterating pages; missing period_yr handled by defaulting to 0 before sorting.
- Sorting is simple comparator logic (period_yr as numeric, then designation), keeping method complexity low (< 5 cyclomatic with small helpers).

3. provenance
- Inputs: docs/task.md
- Assumptions: Use base endpoint /api/asteroids?page={n} to enumerate all items (search API’s “parameter” placeholder is ambiguous); discovery_date year is the 4-digit prefix of the date string; period_yr may be missing or non-numeric → treated as 0; pha filter uses exact Y/N (n/a excluded).
- Checks: Validated constraints (no TDD, no JUnit, no extra classes, 45-min timebox); ensured approach uses only standard JDK 11 APIs; planned pagination + defaulting rules per task.
