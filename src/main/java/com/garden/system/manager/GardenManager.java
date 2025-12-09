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
        // 1. Apply Water Loss
        for (Plant p : gardenPlants) {
            if (p.isAlive()) {
                p.adjustWater(-1);
            }
        }
        // GardenLogger.log("MAINTENANCE: Daily water evaporation (-1) applied.");

        // 2. Trigger Automation
        // Note: With the new HydrationSystem tolerance, this won't refill immediately
        // unless the water is CRITICALLY low.
        checkAndRegulate();
    }

    public void handleRain(int amount) {
        GardenLogger.log("EVENT: Raining " + amount + " units.");
        for (Plant p : gardenPlants) {
            if (p.isAlive()) p.adjustWater(amount);
        }
        // Trigger automation to fix over-watering immediately
        checkAndRegulate();
    }

    public void handleDrought(int intensity) {
        GardenLogger.log("EVENT: Drought condition! Water levels dropping by " + intensity + " units.");
        for (Plant p : gardenPlants) {
            if (p.isAlive()) {
                p.adjustWater(-intensity);
            }
        }
        checkAndRegulate();
    }

    public void handleTemperature(int temp) {
        GardenLogger.log("EVENT: Temperature changed to " + temp + "F.");

        // --- Calculate Evaporation based on Heat ---
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

        climateSystem.regulate(temp);

        for (Plant p : gardenPlants) {
            if (p.isAlive()) {
                p.updateTemperatureReaction(temp);
                if (evaporation > 0) {
                    p.adjustWater(-evaporation);
                }
            }
        }

        checkAndRegulate();
    }

    public void handleParasite(String pestName) {
        GardenLogger.log("EVENT: Parasite '" + pestName + "' detected.");
        pestSystem.deployDefense(pestName, gardenPlants);
        for (Plant p : gardenPlants) {
            if (p.isAlive()) p.attack(pestName);
        }
    }

    public void checkAndRegulate() {
        hydrationSystem.regulate(gardenPlants);
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