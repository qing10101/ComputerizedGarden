package com.garden.system.manager;

import com.garden.system.model.Plant;
import com.garden.system.util.GardenLogger;

import java.util.ArrayList;
import java.util.List;

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

    public long getAliveCount() {
        return gardenPlants.stream().filter(Plant::isAlive).count();
    }

    // --- Simulation Interaction Methods ---

    /**
     * Called once per day cycle to simulate base metabolism.
     */
    public void performDailyMaintenance() {
        GardenLogger.log("MAINTENANCE: Daily base metabolism (-1 water) applied.");
        for (Plant p : gardenPlants) {
            if (p.isAlive()) {
                p.adjustWater(-1); // Decrease water by 1
            }
        }
        // After evaporation, check if we need to auto-water immediately
        hydrationSystem.regulate(gardenPlants);
    }

    public void handleRain(int amount) {
        GardenLogger.log("EVENT: Raining " + amount + " units.");
        for (Plant p : gardenPlants) {
            if (p.isAlive()) p.adjustWater(amount);
        }
        // Trigger automation to fix over-watering immediately
        hydrationSystem.regulate(gardenPlants);
    }

    public void handleDrought(int amount) {
        GardenLogger.log("EVENT: Drought! Water level decreased by " + amount + " units.");
        for (Plant p : gardenPlants) {
            if (p.isAlive()) p.adjustWater(-amount);
        }
        // Trigger automation to fix under-watering immediately
        hydrationSystem.regulate(gardenPlants);
    }

    public void handleTemperature(int temp) {
        GardenLogger.log("EVENT: Temperature changed to " + temp + "F.");

        // --- NEW: Calculate Evaporation based on Heat ---
        int evaporation = 0;
        if (temp >= 100) {
            evaporation = 3; // Extreme heat burns water fast
        } else if (temp >= 90) {
            evaporation = 2; // High heat
        } else if (temp >= 80) {
            evaporation = 1; // Warm
        }

        if (evaporation > 0) {
            GardenLogger.log("ENVIRONMENT: Heat (" + temp + "F) caused extra evaporation (-" + evaporation + " water).");
        }

        climateSystem.regulate(temp); // Automation handles environment

        for (Plant p : gardenPlants) {
            if (p.isAlive()) {
                p.updateTemperatureReaction(temp);
                // Apply heat-based evaporation
                if (evaporation > 0) {
                    p.adjustWater(-evaporation);
                }
            }
        }

        // Trigger automation to check if plants need water after evaporation
        hydrationSystem.regulate(gardenPlants);
    }

    public void handleParasite(String pestName) {
        GardenLogger.log("EVENT: Parasite '" + pestName + "' detected.");
        // First, let pests attack plants
        for (Plant p : gardenPlants) {
            if (p.isAlive()) p.attack(pestName);
        }
        // Then deploy defense system to fight back
        pestSystem.deployDefense(pestName, gardenPlants);
    }

    // --- Manual Device Controls ---

    public void activateHeater() {
        climateSystem.turnHeaterOn();
    }

    public void deactivateHeater() {
        climateSystem.turnHeaterOff();
    }

    public void activateCooler() {
        climateSystem.turnCoolerOn();
    }

    public void deactivateCooler() {
        climateSystem.turnCoolerOff();
    }

    // --- Manual intervention methods for individual plants ---
    public Plant findPlantByName(String name) {
        for (Plant p : gardenPlants) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public boolean removePestFromPlant(String plantName) {
        Plant plant = findPlantByName(plantName);
        if (plant != null && plant.isAlive()) {
            return plant.removePest();
        }
        return false;
    }

    public boolean waterPlant(String plantName, int amount) {
        Plant plant = findPlantByName(plantName);
        if (plant != null && plant.isAlive()) {
            plant.manualWater(amount);
            return true;
        }
        return false;
    }

    public boolean healPlant(String plantName, int amount) {
        Plant plant = findPlantByName(plantName);
        if (plant != null && plant.isAlive()) {
            plant.heal(amount);
            return true;
        }
        return false;
    }

    public boolean applyFertilizerToPlant(String plantName) {
        Plant plant = findPlantByName(plantName);
        if (plant != null && plant.isAlive()) {
            plant.applyFertilizer();
            return true;
        }
        return false;
    }

    public boolean emergencyTreatmentForPlant(String plantName) {
        Plant plant = findPlantByName(plantName);
        if (plant != null && plant.isAlive()) {
            plant.emergencyTreatment();
            return true;
        }
        return false;
    }
}