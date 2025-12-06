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

    // Manual intervention methods for individual plants
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
