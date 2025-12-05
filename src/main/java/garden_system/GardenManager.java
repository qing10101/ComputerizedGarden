package garden_system;

import java.util.*;

// Module 1: Hydration System
class HydrationSystem {
    public void regulate(List<Plant> plants) {
        for (Plant p : plants) {
            if (!p.isAlive()) continue;

            if (p.getCurrentWaterLevel() < p.getWaterRequirement()) {
                GardenLogger.log("AUTOMATION: Sprinklers activated for " + p.getName());
                p.adjustWater(5);
            } else if (p.getCurrentWaterLevel() > p.getWaterRequirement() + 10) {
                GardenLogger.log("AUTOMATION: Drainage opened for " + p.getName());
                p.adjustWater(-5);
            }
        }
    }
}

// Module 2: Climate Control System (Heating/Cooling)
class ClimateControlSystem {
    public void regulate(int currentTemp) {
        if (currentTemp < 50) {
            GardenLogger.log("AUTOMATION: Heater activated. Warming garden.");
        } else if (currentTemp > 90) {
            GardenLogger.log("AUTOMATION: Misting fans activated. Cooling garden.");
        }
    }
}

// Module 3: Pest Defense System
class PestDefenseSystem {
    public void deployDefense(String detectedPest, List<Plant> plants) {
        boolean threadDetected = false;
        for(Plant p : plants) {
            if(p.isAlive() && p.getVulnerableTo().contains(detectedPest)) {
                threadDetected = true;
                // Heal the plant slightly as we deploy countermeasures
                p.heal(10);
            }
        }
        if(threadDetected) {
            GardenLogger.log("AUTOMATION: Pesticide deployed for " + detectedPest);
        }
    }
}

public class GardenManager {
    private static GardenManager instance;
    private List<Plant> gardenPlants;

    // Subsystems
    private HydrationSystem hydrationSystem;
    private ClimateControlSystem climateSystem;
    private PestDefenseSystem pestSystem;

    private GardenManager() {
        gardenPlants = new ArrayList<>();
        hydrationSystem = new HydrationSystem();
        climateSystem = new ClimateControlSystem();
        pestSystem = new PestDefenseSystem();
    }

    public static GardenManager getInstance() {
        if (instance == null) instance = new GardenManager();
        return instance;
    }

    public void clearGarden() {
        gardenPlants.clear();
    }

    public void addPlant(Plant p) {
        gardenPlants.add(p);
    }

    public List<Plant> getPlants() {
        return gardenPlants;
    }

    // --- Simulation Interaction Methods ---

    public void handleRain(int amount) {
        GardenLogger.log("EVENT: Raining " + amount + " units.");
        for (Plant p : gardenPlants) {
            if (p.isAlive()) p.adjustWater(amount);
        }
        // Trigger automation to fix over-watering immediately
        hydrationSystem.regulate(gardenPlants);
    }

    public void handleTemperature(int temp) {
        GardenLogger.log("EVENT: Temperature changed to " + temp + "F.");
        climateSystem.regulate(temp); // Automation handles environment
        for (Plant p : gardenPlants) {
            if (p.isAlive()) p.updateTemperatureReaction(temp);
        }
    }

    public void handleParasite(String pestName) {
        GardenLogger.log("EVENT: Parasite '" + pestName + "' detected.");
        pestSystem.deployDefense(pestName, gardenPlants); // Automation fights back
        for (Plant p : gardenPlants) {
            if (p.isAlive()) p.attack(pestName);
        }
    }
}
