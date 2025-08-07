Release Notes — Asteroid Monitoring Station (Java 11)

Implemented
- Functional
  - F1 Paging: Fetches https://jsonmock.hackerrank.com/api/asteroids?page={n} iterating up to total_pages.
  - F2 Filtering: Keeps only records where discovery year == input year and pha equals input ("Y" or "N"), case-insensitive.
  - F3 Normalization: Treats missing/non-numeric period_yr as 0.0.
  - F4 Sorting: Ascending by period_yr; tie-breaker ascending designation.
  - F5 Output: Returns list of designations in the required order.
- Non-Functional
  - Single-class solution in src/main/java/pl/darulec/Result.java (no additional classes).
  - No external dependencies; only Java 11 HttpClient and regex.
  - Deterministic behavior with conservative failure handling (graceful degrade).
  - Within 45-minute scope; minimal parsing limited to required fields.

Known Gaps / Deferred
- No retry/backoff; relies on HttpClient defaults.
- Minimal JSON parsing (object slicing inside data array + regex for field extraction); not a full JSON parser.
- No logging/metrics to keep within constraints/timebox.
- If API returns non-200 mid-paging, stops and returns gathered, sorted subset (documented behavior).
- Does not use server-side /search due to ambiguous semantics; filters locally after fetching.

Assumptions
- discovery_date begins with YYYY (or YYYY-MM-DD); otherwise year extraction fails and the record is skipped.
- pha filter is strictly "Y" or "N" (case-insensitive); "n/a" never matches.
- period_yr can be parsed as double; defaults to 0.0 on failure or missing.

Build and Run
- Build: mvn -q -DskipTests package
- Use: Call pl.darulec.Result.asteroidMonitor(int year, String pha) to obtain List<String> of designations.
- Demo: The class includes a trivial main printing "hello :)"; adjust as needed to invoke asteroidMonitor in your environment.

File Changes
- src/main/java/pl/darulec/Result.java: Implemented asteroidMonitor and minimal helpers; kept single-class constraint.
- docs/*.md: Added goal, risks, risksAnswers, strategy, requirements, cautions, proposedArchitecture.

Provenance
- Inputs: docs/task.md, solver/workflow.txt
- Checks performed:
  - Sorting order and normalization rules align with docs/task.md.
  - Single-class, no tests, no external libs enforced by repository constraints.
  - Deterministic, conservative error handling documented.
