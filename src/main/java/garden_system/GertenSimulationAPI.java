package garden_system;

import java.util.*;

public class GertenSimulationAPI {

    private GardenManager manager;

    public GertenSimulationAPI() {
        this.manager = GardenManager.getInstance();
    }

    public void initializeGarden() {
        try {
            GardenLogger.log("API CALL: initializeGarden()");
            manager.clearGarden();

            // Updated with "Type" string
            manager.addPlant(new Plant("Rose A", "Rose", 10, Arrays.asList("aphids", "beetles")));
            manager.addPlant(new Plant("Tomato 1", "Tomato", 15, Arrays.asList("worms", "blight")));
            manager.addPlant(new Plant("Herb Patch", "Basil", 8, Arrays.asList("slugs")));
            manager.addPlant(new Plant("Desert King", "Cactus", 2, Arrays.asList("rot")));

            GardenLogger.log("Garden initialized with default configuration.");
            GardenApp.refreshUI();

        } catch (Exception e) {
            GardenLogger.log("ERROR in initializeGarden: " + e.getMessage());
        }
    }

    public Map<String, Object> getPlants() {
        // (Keep existing implementation logic)
        GardenLogger.log("API CALL: getPlants()");
        Map<String, Object> result = new HashMap<>();
        // ... (Simplified for brevity, same as before)
        return result;
    }

    // Pass-through methods
    public void rain(int amount) { manager.handleRain(amount); GardenApp.refreshUI(); }
    public void temperature(int temp) { manager.handleTemperature(temp); GardenApp.refreshUI(); }
    public void parasite(String str) { manager.handleParasite(str); GardenApp.refreshUI(); }
    public void getState() {
        // Just triggers the log
        manager.getPlants().forEach(p ->
                GardenLogger.log(String.format("STATE: %s | Health: %d | Water: %d", p.getName(), p.getHealth(), p.getCurrentWaterLevel()))
        );
    }
}