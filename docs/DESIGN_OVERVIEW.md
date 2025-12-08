# Design Overview

## Layers
- UI: `GardenApp` (JavaFX) builds the interface, handles user events, refreshes plant cards.
- API / Façade: `GertenSimulationAPI` exposes initialize/rain/temperature/parasite/getPlants/getState/heartbeat/registerMonitor.
- Orchestration: `GardenManager` holds plants and routes to subsystems and single-plant operations.
- Subsystems: `HydrationSystem` (water regulation), `ClimateControlSystem` (temperature logs), `PestDefenseSystem` (pest defense + minor heal).
- Devices/Sensors: `Sprinkler`, `Heater`, `Cooler`, `PestTrap`, `MoistureSensor`, `TempSensor` (lightweight, log device/sensor actions).
- Domain model: `Plant` (state/behaviors: water, health, pest, emergency).
- Infrastructure: `GardenLogger` (logging), `MonitoringService` (heartbeat + external monitor hook), `ConfigParser` (config parsing).

## Data Flow
1) Init: `initializeGarden()` → config seeding → `GardenManager` → `GardenApp.refreshUI()`
2) Events: `rain/temperature/parasite` → `GardenManager.handle*` → subsystems/plants update → `refreshUI()` → logs
3) Manual care: UI card click → `showPlantActionsDialog` → `GardenManager` single-plant method → refresh UI
4) State query/monitoring: `getState()` logs summary and per-plant status; `heartbeat()` logs snapshot and can call external monitor via `registerMonitor`.

## Configuration
- `src/main/resources/garden-config.json`: plant seeding config (name/type/waterRequirement/pests).
- Parsed via `ConfigParser` (lightweight, no extra deps); falls back to built-in defaults if absent/invalid.

## Logging
- Central entry: `GardenLogger.log(...)`; UI callback + file.
- Use `GardenLogger.logEvent(level,module,msg)` for structured prefixes (monitoring uses this).

## Defensive Choices
- `refreshUI()` safely no-ops when UI not ready (for headless API use).
- Input clamping on temperature/rain to match API expectations and avoid crashes.
- Default seeding fallback ensures initialization succeeds even without config.
