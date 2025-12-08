# Testing Plan

> Manual/targeted checklist aligned with API requirements.

## 1) Smoke / UI
- Run `./mvnw javafx:run`.
- Verify default plants load; plant cards clickable; dialog actions (water/heal/fertilizer/remove pest/emergency) work without errors.
- Toggle Auto Run; ensure it advances and logs.

## 2) API sequence (grading-script style)
1. Call `initializeGarden()`.
2. Call `rain/temperature/parasite` in any order 24 times (simulated 24 hours/days).
3. Call `getState()` and check logs include:
   - `STATE SUMMARY` with totals/alive, simulatedHours/day.
   - `STATE:` lines with alive/health/water/pest per plant.
4. (Optional) Register a monitor callback and call `heartbeat()` to verify external monitor is invoked.

## 3) Defensive inputs
- Temperature below 40 or above 120: clamped, no exception.
- Rain extremely high: clamped, no exception.
- `parasite(null/empty)`: handled and logged, no exception.

## 4) Stability sanity
- Let Auto Run run 1–2 hours; confirm no crash, log growth acceptable.

## 5) Future unit tests (if added)
- `Plant`: water adjust/normalize, attack/heal/emergency, pest flags.
- `HydrationSystem`: boundary water regulation.
- `PestDefenseSystem`: applies heal to vulnerable plants.
- `ConfigParser`: valid/invalid JSON inputs.
