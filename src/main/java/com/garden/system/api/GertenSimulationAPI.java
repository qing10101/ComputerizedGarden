package com.garden.system.api;

import com.garden.system.manager.GardenManager;
import com.garden.system.model.Plant;
import com.garden.system.ui.GardenApp;
import com.garden.system.util.ConfigParser;
import com.garden.system.util.GardenLogger;
import com.garden.system.util.MonitoringService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GertenSimulationAPI {

    private GardenManager manager;
    private final MonitoringService monitoringService = new MonitoringService();
    private int simulatedHours = 0; // Each event call represents 1 simulated hour

    public GertenSimulationAPI() {
        this.manager = GardenManager.getInstance();
    }

    public void initializeGarden() {
        try {
            GardenLogger.logEvent("INFO", "API", "initializeGarden()");
            manager.clearGarden();
            simulatedHours = 0;

            boolean loaded = loadConfigFromResource();
            if (!loaded) {
                seedDefaultPlants();
                GardenLogger.logEvent("INFO", "CONFIG", "Using default hardcoded plant set.");
            } else {
                GardenLogger.logEvent("INFO", "CONFIG", "Loaded plants from resources/garden-config.json");
            }

            GardenLogger.logEvent("INFO", "API", "Garden initialized");
            GardenApp.refreshUI();

        } catch (Exception e) {
            GardenLogger.logEvent("ERROR", "API", "initializeGarden failed: " + e.getMessage());
        }
    }

    public Map<String, Object> getPlants() {
        GardenLogger.logEvent("INFO", "API", "getPlants()");
        List<Plant> plants = manager.getPlants();
        Map<String, Object> result = new HashMap<>();
        result.put("plants", plants.stream().map(Plant::getName).collect(Collectors.toList()));
        result.put("waterRequirement", plants.stream().map(Plant::getWaterRequirement).collect(Collectors.toList()));
        result.put("parasites", plants.stream().map(Plant::getVulnerableTo).collect(Collectors.toList()));
        return result;
    }

    // Pass-through methods
    public void rain(int amount) {
        try {
            int safeAmount = Math.max(0, Math.min(amount, 100)); // simple clamp
            advanceClock();
            GardenLogger.logEvent("INFO", "API", String.format("rain(%d)", safeAmount));
            manager.handleRain(safeAmount);
            GardenApp.refreshUI();
        } catch (Exception e) {
            GardenLogger.logEvent("ERROR", "API", "rain failed: " + e.getMessage());
        }
    }

    public void temperature(int temp) {
        try {
            int safeTemp = Math.max(40, Math.min(temp, 120)); // enforce documented range
            advanceClock();
            GardenLogger.logEvent("INFO", "API", String.format("temperature(%d)", safeTemp));
            manager.handleTemperature(safeTemp);
            GardenApp.refreshUI();
        } catch (Exception e) {
            GardenLogger.logEvent("ERROR", "API", "temperature failed: " + e.getMessage());
        }
    }

    public void parasite(String str) {
        try {
            String pest = (str == null || str.isBlank()) ? "unknown" : str;
            advanceClock();
            GardenLogger.logEvent("INFO", "API", String.format("parasite(%s)", pest));
            manager.handleParasite(pest);
            GardenApp.refreshUI();
        } catch (Exception e) {
            GardenLogger.logEvent("ERROR", "API", "parasite failed: " + e.getMessage());
        }
    }

    public void getState() {
        try {
            GardenLogger.logEvent("INFO", "API", "getState()");
            int day = simulatedHours / 24;
            GardenLogger.logEvent("INFO", "STATE",
                    String.format("SUMMARY simulatedHours=%d day=%d total=%d alive=%d",
                            simulatedHours, day, manager.getPlants().size(), manager.getAliveCount()));
            manager.getPlants().forEach(p -> GardenLogger.logEvent("INFO", "STATE", String.format(
                    "PLANT name=%s type=%s alive=%s health=%d water=%d/%d pest=%s",
                    p.getName(), p.getType(), p.isAlive(), p.getHealth(), p.getCurrentWaterLevel(),
                    p.getWaterRequirement(), p.hasPest() ? p.getCurrentPest() : "none"
            )));
        } catch (Exception e) {
            GardenLogger.logEvent("ERROR", "API", "getState failed: " + e.getMessage());
        }
    }

    public void heartbeat(int dayCount) {
        try {
            // Periodic check and regulation of water levels
            manager.checkAndRegulate();
            GardenApp.refreshUI();
            monitoringService.heartbeat(dayCount);
        } catch (Exception e) {
            GardenLogger.logEvent("ERROR", "Monitor", "heartbeat failed: " + e.getMessage());
        }
    }

    public void registerMonitor(MonitoringService.MonitorClient client) {
        monitoringService.registerMonitorClient(client);
    }

    private void advanceClock() {
        simulatedHours++;
    }

    private boolean loadConfigFromResource() {
        try (InputStream is = getClass().getResourceAsStream("/garden-config.json")) {
            if (is == null) return false;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String json = reader.lines().collect(Collectors.joining("\n"));
                List<Map<String, Object>> plants = ConfigParser.parsePlants(json);
                if (plants == null || plants.isEmpty()) return false;
                for (Map<String, Object> p : plants) {
                    String name = (String) p.getOrDefault("name", "Plant");
                    String type = (String) p.getOrDefault("type", "Generic");
                    int water = ((Number) p.getOrDefault("waterRequirement", 10)).intValue();
                    @SuppressWarnings("unchecked")
                    List<String> pests = (List<String>) p.getOrDefault("pests", Collections.emptyList());
                    manager.addPlant(new Plant(name, type, water, pests));
                }
                return true;
            }
        } catch (Exception e) {
            GardenLogger.log("WARN: Failed to load garden-config.json: " + e.getMessage());
            return false;
        }
    }

    private void seedDefaultPlants() {
        manager.addPlant(new Plant("Rose A", "Rose", 10, Arrays.asList("aphids", "beetles")));
        manager.addPlant(new Plant("Tomato 1", "Tomato", 15, Arrays.asList("worms", "blight")));
        manager.addPlant(new Plant("Herb Patch", "Basil", 8, Arrays.asList("slugs")));
        manager.addPlant(new Plant("Desert King", "Cactus", 2, Arrays.asList("rot")));
        manager.addPlant(new Plant("Sun Glory", "Sunflower", 12, Arrays.asList("birds")));
        manager.addPlant(new Plant("Gold Corn", "Corn", 20, Arrays.asList("locusts")));
        manager.addPlant(new Plant("Tulip Field", "Tulip", 10, Arrays.asList("aphids")));
    }
}
