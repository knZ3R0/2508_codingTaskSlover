# Proposed Architecture

Layering (conceptual; all implemented within a single class via private helpers)
- API → Service (Filter/Sort) → Infra (HTTP/Parsing)

C1 (API/Entrypoint):
- Does: Exposes asteroidMonitor(year, pha); validates inputs and orchestrates pagination, parsing, filtering, sorting, and return values.
- Exposes: List<String> asteroidMonitor(int year, String pha)
- Hides: HTTP and JSON parsing details
- Talks to: C3 (Service) and C2 (Infra helpers)
- Mock points: Method indirection (httpGet)

C2 (Infra/HTTP+Parsing):
- Does: Performs HTTP GET requests and minimal JSON parsing.
- Exposes: httpGet(url), parseTotalPages(json), extractObjectsFromDataArray(json), extractStringField(obj, field)
- Hides: Regex/string parsing internals
- Talks to: C1 and C3
- Mock points: httpGet

C3 (Service/Filter+Sort):
- Does: Applies year and PHA filters; converts period_yr to numeric (default 0), sorts by (period_yr asc, designation asc), maps to designations.
- Exposes: filterAndSort(entries, year, pha)
- Hides: Numeric conversions and comparator construction
- Talks to: C1 and C2
- Mock points: Pure functions

Component Graph (≤ 3 layers)
API (C1) → Service (C3) → Infra (C2)

Potential God Component
- Single-class constraint risks a large C1. Mitigation: keep helpers small and cohesive (C2/C3 as private static methods).

Contract Notes (public method)
- asteroidMonitor(year, pha)
  - Preconditions:
    - pha equals "Y" or "N" (case-insensitive). Invalid → return empty list.
    - year must be a reasonable 4-digit integer (defensive only; non-matching dates naturally filter out).
  - Postconditions:
    - Returns designations of asteroids discovered in the given year with matching PHA flag, sorted by period_yr asc then designation asc.
    - On HTTP/IO failure or parse failure: returns empty list (fail-safe).
  - MOCKABLE: HTTP boundary via httpGet(String url) indirection.

## Provenance
- Inputs: docs/task.md, docs/goal.md, docs/risks.md, docs/risksAnswers.md, docs/strategy.md, docs/requirements.md
- Assumptions: Base endpoint pagination is canonical; strict Y/N PHA; discovery_date is "YYYY-MM-DD".
- Checks: Architecture respects no extra classes/deps; small private helpers used as seams; ≤ 3 layers.
