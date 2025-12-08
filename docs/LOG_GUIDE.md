# Log Guide

## 1. Location & Format
- File: `log.txt` in project root (append-only).
- UI: Live view in the log pane.
- Timestamp prefix: `[HH:mm:ss]`; some entries include module prefixes.

## 2. Key Prefixes
- `API CALL: ...` External or button-triggered API calls.
- `EVENT:` Environmental events (rain/temperature/pest).
- `AUTOMATION:` Subsystem actions (watering/temperature/pest defense).
- `ACTION:` Manual/single-plant actions (water/heal/fertilizer/emergency/remove pest).
- `STATE SUMMARY` + `STATE:` Aggregated and per-plant status (alive, health, water, pest).
- `[INFO][Monitor] ...` Heartbeat/monitor outputs.
- `[INFO][Device] ...` Device actions (sprinkler/heater/cooler/pest trap).
- `[INFO][Sensor] ...` Sensor readings (moisture/temperature).
- `WARNING/ERROR/FATAL` Problems.

## 3. Typical snippets
- `API CALL: rain(10)`
- `EVENT: Raining 10 units.`
- `AUTOMATION: Sprinklers activated for Rose A (+3 units)`
- `STATE SUMMARY: simulatedHours=24 day=1 total=7 alive=7`
- `[INFO][Monitor] Heartbeat day=2 plants=7 alive=7`

## 4. Navigation/Troubleshooting
- Filter by module: `rg "AUTOMATION"` or `rg "Monitor" log.txt`
- Inspect a plant: search `STATE: name=<plant>`
- Errors: `rg "ERROR|FATAL|WARNING" log.txt`

## 5. Notes
- Long runs grow `log.txt`; rotate/clean if needed.
- For more structure, use `GardenLogger.logEvent(level,module,msg)` and/or emit JSON lines.
