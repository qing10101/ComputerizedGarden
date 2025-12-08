package com.garden.system.manager;

import com.garden.system.model.Plant;
import com.garden.system.util.GardenLogger;

import java.util.ArrayList;
import java.util.List;

public class GardenManager {
    private static GardenManager instance;
    private List<Plant> gardenPlants;
    private int currentTemperature = 70; // Default temperature

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
        currentTemperature = temp;
        climateSystem.regulate(temp); // Automation handles environment
        // Apply temperature effects to plants based on actual regulated temperature
        int effectiveTemp = climateSystem.getEffectiveTemperature(temp);
        for (Plant p : gardenPlants) {
            if (p.isAlive()) p.updateTemperatureReaction(effectiveTemp);
        }
        currentTemperature = effectiveTemp; // Update to effective temperature after regulation
    }

    public void handleParasite(String pestName) {
        GardenLogger.log("EVENT: Parasite '" + pestName + "' detected.");
        pestSystem.deployDefense(pestName, gardenPlants); // Automation fights back
        for (Plant p : gardenPlants) {
            if (p.isAlive()) p.attack(pestName);
        }
    }

    // --- Periodic Maintenance ---
    public void checkAndRegulate() {
        // Automatically check and regulate water levels for all plants
        hydrationSystem.regulate(gardenPlants);
        // Automatically check and regulate temperature
        climateSystem.regulate(currentTemperature);
        // Update effective temperature and apply to plants
        int effectiveTemp = climateSystem.getEffectiveTemperature(currentTemperature);
        if (effectiveTemp != currentTemperature) {
            currentTemperature = effectiveTemp;
            for (Plant p : gardenPlants) {
                if (p.isAlive()) p.updateTemperatureReaction(effectiveTemp);
            }
        }
    }

    public int getCurrentTemperature() {
        return currentTemperature;
    }

    // --- Manual Device Controls (Updated) ---

    public void activateHeater() {
        climateSystem.turnHeaterOn();
        // Update temperature when manually controlling devices
        int effectiveTemp = climateSystem.getEffectiveTemperature(currentTemperature);
        if (effectiveTemp != currentTemperature) {
            currentTemperature = effectiveTemp;
            for (Plant p : gardenPlants) {
                if (p.isAlive()) p.updateTemperatureReaction(effectiveTemp);
            }
        }
    }

    public void deactivateHeater() {
        climateSystem.turnHeaterOff();
    }

    public void activateCooler() {
        climateSystem.turnCoolerOn();
        // Update temperature when manually controlling devices
        int effectiveTemp = climateSystem.getEffectiveTemperature(currentTemperature);
        if (effectiveTemp != currentTemperature) {
            currentTemperature = effectiveTemp;
            for (Plant p : gardenPlants) {
                if (p.isAlive()) p.updateTemperatureReaction(effectiveTemp);
            }
        }
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