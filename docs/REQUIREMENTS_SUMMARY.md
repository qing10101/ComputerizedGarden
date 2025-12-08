# Requirements Fit (Requirements.pdf & Gardening System APIs.pdf)

## Musts (Requirements.pdf)
- JavaFX UI: Implemented (`GardenApp`).
- ≥3 modules: HydrationSystem, ClimateControlSystem, PestDefenseSystem.
- Big garden / multiple plants: Default seed of 7 diverse plants; UI allows adding more.
- Logging: `log.txt` + UI log.
- Devices/Sensors: Lightweight sprinkler/heater/cooler/pest trap + moisture/temp sensors integrated with subsystems.
- Monitoring hook: Heartbeat logging and external monitor registration.

## API Compliance (Gardening System APIs.pdf)
- `initializeGarden()`: Seeds from `garden-config.json` (fallback to defaults); resets simulated clock.
- `getPlants()`: Returns map with `plants`, `waterRequirement`, `parasites` lists.
- `rain(int)`: Clamped input, advances simulated hour, logs call.
- `temperature(int)`: Clamped to 40–120F, advances hour, logs call.
- `parasite(String)`: Advances hour, logs call.
- `getState()`: Logs simulated hours/day, totals/alive, and per-plant status.
- Logging requirement: satisfied.
- Monitoring: `heartbeat()` logs summary; `registerMonitor` allows external monitor callback.
