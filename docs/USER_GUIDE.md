# Computerized Garden - User Guide

## 1. Launch
- Requirements: JDK 21+, Maven wrapper bundled.
- Run from project root: `./mvnw javafx:run`.
- On launch, the garden is seeded from `src/main/resources/garden-config.json` (falls back to built-in defaults if missing/invalid).

## 2. UI Layout
- Top: Title and Day counter.
- Left: “New Plant” form (name + type).
- Center: Plant grid (click a live plant card to open the action dialog).
- Bottom: Simulation controls.
- Right/bottom: Log panel (system activity, API calls, heartbeats).

## 3. Operations
- Add plant: Enter name + choose type, click “Plant It”.
- Manual simulation: “🌞 Simulate Full Day”.
- Auto simulation: “⏱ Auto Run” toggles periodic simulation (defaults to every 10s).
- Environment events: “🌧 Rain / 🔥 Heat / 🐛 Pest” trigger API calls.
- Plant care: Click a plant card → dialog actions (remove pest, water, heal, fertilizer, emergency).

## 4. Logging
- Live view in UI log; persisted to `log.txt` (project root).
- Common prefixes:
  - `API CALL: ...` external/button-triggered API calls
  - `EVENT:` rain/temp/pest events
  - `AUTOMATION:` subsystem adjustments
  - `STATE SUMMARY` + `STATE:` aggregated and per-plant state
  - `[INFO][Monitor]` heartbeat/monitor messages

## 5. Configuration
- File: `src/main/resources/garden-config.json`
```json
{
  "plants": [
    { "name": "Rose A", "type": "Rose", "waterRequirement": 10, "pests": ["aphids"] }
  ]
}
```
- If missing/invalid, the app seeds a default set.

## 6. FAQ
- `log.txt` keeps changing in git status: run `git rm --cached log.txt` once, then commit.
- Headless API usage: `refreshUI()` is a no-op when UI isn't initialized; API calls won't throw due to missing UI.
- Monitoring hook: if you need external monitoring, implement `MonitoringService.MonitorClient` and register via `GertenSimulationAPI.registerMonitor(client)`; heartbeats are triggered after each simulated day in the UI auto/day cycle.

## 7. Quick commands
- Run UI: `./mvnw javafx:run`
- Compile only: `./mvnw -q -DskipTests compile`
