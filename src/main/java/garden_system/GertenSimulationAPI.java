package garden_system;

import java.util.*;

public class GertenSimulationAPI {

    private GardenManager manager;

    public GertenSimulationAPI() {
        this.manager = GardenManager.getInstance();
    }

    /**
     * Initializes the garden with a predefined set of plants.
     * Simulates reading from a config file.
     */
    public void initializeGarden() {
        try {
            GardenLogger.log("API CALL: initializeGarden()");
            manager.clearGarden();

            // Simulate loading from a config file
            manager.addPlant(new Plant("Rose", 10, Arrays.asList("aphids", "beetles")));
            manager.addPlant(new Plant("Tomato", 15, Arrays.asList("worms", "blight")));
            manager.addPlant(new Plant("Basil", 8, Arrays.asList("slugs")));
            manager.addPlant(new Plant("Cactus", 2, Arrays.asList("rot")));

            GardenLogger.log("Garden initialized successfully with default configuration.");

            // Update UI if running
            GardenApp.refreshUI();

        } catch (Exception e) {
            GardenLogger.log("ERROR in initializeGarden: " + e.getMessage());
        }
    }

    /**
     * Returns a map containing plant details.
     */
    public Map<String, Object> getPlants() {
        GardenLogger.log("API CALL: getPlants()");
        Map<String, Object> result = new HashMap<>();

        List<String> names = new ArrayList<>();
        List<Integer> waterReqs = new ArrayList<>();
        List<List<String>> parasites = new ArrayList<>();

        for (Plant p : manager.getPlants()) {
            names.add(p.getName());
            waterReqs.add(p.getWaterRequirement());
            parasites.add(p.getVulnerableTo());
        }

        result.put("plants", names);
        result.put("waterRequirement", waterReqs);
        result.put("parasites", parasites);

        return result;
    }

    /**
     * Simulates rainfall.
     */
    public void rain(int amount) {
        try {
            manager.handleRain(amount);
            GardenApp.refreshUI();
        } catch (Exception e) {
            GardenLogger.log("ERROR in rain(): " + e.getMessage());
        }
    }

    /**
     * Handles temperature data.
     */
    public void temperature(int temp) {
        try {
            manager.handleTemperature(temp);
            GardenApp.refreshUI();
        } catch (Exception e) {
            GardenLogger.log("ERROR in temperature(): " + e.getMessage());
        }
    }

    /**
     * Triggers parasite infestation.
     */
    public void parasite(String str) {
        try {
            manager.handleParasite(str);
            GardenApp.refreshUI();
        } catch (Exception e) {
            GardenLogger.log("ERROR in parasite(): " + e.getMessage());
        }
    }

    /**
     * Logs details about the garden's current state.
     */
    public void getState() {
        try {
            GardenLogger.log("API CALL: getState() - GENERATING REPORT");
            StringBuilder sb = new StringBuilder();
            sb.append("--- GARDEN STATE REPORT ---\n");

            int aliveCount = 0;
            for (Plant p : manager.getPlants()) {
                String status = p.isAlive() ? "ALIVE (Health: " + p.getHealth() + ")" : "DEAD";
                sb.append(String.format("Plant: %-10s | Status: %s | Water: %d/%d\n",
                        p.getName(), status, p.getCurrentWaterLevel(), p.getWaterRequirement()));
                if (p.isAlive()) aliveCount++;
            }
            sb.append("Total Alive: " + aliveCount + "/" + manager.getPlants().size());

            GardenLogger.log(sb.toString());
        } catch (Exception e) {
            GardenLogger.log("ERROR in getState(): " + e.getMessage());
        }
    }
}
