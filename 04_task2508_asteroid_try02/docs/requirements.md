# Requirements

## Functional
F1. Fetch pagination metadata → Inputs: GET page 1 JSON → Outputs: total_pages (int) → Primary: total_pages parsed or 0 if missing.  
Test: T01 Parse total_pages from page 1 or default to 0.

F2. Iterate pages and fetch data → Inputs: total_pages, GET JSON per page → Outputs: raw JSON strings → Primary: loop 1..total_pages without error.  
Test: T02 Loop pages 1..N and collect JSON payloads.

F3. Parse asteroid fields → Inputs: page JSON → Outputs: designation, discovery_date, period_yr, pha per record → Primary: extract required fields ignoring others.  
Test: T03 Extract fields for each asteroid object; ignore unknowns.

F4. Filter by year and PHA → Inputs: extracted records, year (int), pha (Y/N) → Outputs: filtered list → Primary: discovery_date year equals input; pha equals input (case-insensitive).  
Test: T04 Filter keeps only matching year and pha; excludes others.

F5. Default period_yr → Inputs: string period_yr → Outputs: numeric double → Primary: treat missing/non-numeric as 0.  
Test: T05 Default non-numeric/missing period_yr to 0.

F6. Sorting → Inputs: filtered list with numeric period → Outputs: sorted list → Primary: sort by period_yr asc, then designation asc.  
Test: T06 Stable ordering by (period, designation).

F7. Return designations → Inputs: sorted records → Outputs: List<String> → Primary: designation list in correct order.  
Test: T07 Return only designations.

## Non-Functional
NF1. Dependencies constraint → Metric: No external JSON/HTTP libs → Method: Inspect imports (JDK 11 only).  
Test: T08 Verify only java.* imports used.

NF2. Robustness to network errors → Metric: No uncaught exceptions; returns empty list on failure → Method: induce error (bad URL) and run.  
Test: T09 Function returns empty list on HTTP/IO error.

NF3. Performance (dataset size) → Metric: Completes within a few seconds for ~21 pages/202 items → Method: manual timing.  
Test: T10 End-to-end run doesn’t time out.

NF4. Maintainability within single class → Metric: Cyclomatic complexity per method < 5; small helpers used → Method: code review.  
Test: T11 Each method is concise; helpers isolate parsing.

NF5. Deterministic output → Metric: Same inputs produce identical ordering → Method: repeat runs.  
Test: T12 Two runs yield identical lists.

## Conflicts
WARNING-CONFLICT: Task mentions search endpoint but repo dataset confirms full enumeration via base endpoint.  
Reconciliation: Use base endpoint pagination; ignore /search to avoid ambiguity.

## Provenance
- Inputs: docs/task.md, docs/goal.md, docs/risks.md, docs/risksAnswers.md, docs/strategy.md, docs/asteroids_v2.txt
- Assumptions: Base endpoint is canonical; discovery_date uses YYYY-MM-DD; pha is Y/N; default period_yr=0 if non-numeric/missing.
- Checks: Requirements mapped to tests; constraints reflect no extra classes/deps and 45-min timebox.
