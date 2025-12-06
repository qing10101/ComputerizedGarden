package com.garden.system.model;

import com.garden.system.util.GardenLogger;

import java.util.List;

public class Plant {
    private String name;
    private String type; // New field for icon logic
    private int waterRequirement;
    private int currentWaterLevel;
    private List<String> vulnerableTo;
    private boolean isAlive;
    private int health;
    private String currentPest; // Track if plant is currently infested

    public Plant(String name, String type, int waterRequirement, List<String> vulnerableTo) {
        this.name = name;
        this.type = type; // Store the type
        this.waterRequirement = waterRequirement;
        this.vulnerableTo = vulnerableTo;
        this.currentWaterLevel = waterRequirement;
        this.isAlive = true;
        this.health = 100;
    }

    public void adjustWater(int amount) {
        this.currentWaterLevel += amount;
        normalizeWaterLevel(); // Ensure water level stays within healthy bounds
        checkHealth();
    }

    // Normalize water level to keep it within healthy bounds
    private void normalizeWaterLevel() {
        // Set minimum to 0
        if (currentWaterLevel < 0) {
            currentWaterLevel = 0;
        }
        
        // Set maximum to waterRequirement * 1.5 (150% of requirement)
        int maxWater = (int)(waterRequirement * 1.5);
        if (currentWaterLevel > maxWater) {
            int excess = currentWaterLevel - maxWater;
            currentWaterLevel = maxWater;
            GardenLogger.log("WARNING: " + name + " water level exceeded maximum (" + maxWater + "). Excess water (" + excess + " units) drained.");
        }
    }

    public void attack(String parasite) {
        if (vulnerableTo.contains(parasite)) {
            this.health -= 30;
            this.currentPest = parasite; // Mark plant as infested
            GardenLogger.log("WARNING: " + name + " (" + type + ") attacked by " + parasite + "! Health: " + health);
        } else {
            GardenLogger.log("INFO: " + name + " is immune to " + parasite + ".");
        }
        checkHealth();
    }

    public void updateTemperatureReaction(int temp) {
        if (temp < 40 || temp > 100) {
            this.health -= 10;
            GardenLogger.log("WARNING: " + name + " hurting from temp (" + temp + "F).");
        }
        checkHealth();
    }

    private void checkHealth() {
        // Ensure water level is normalized first
        normalizeWaterLevel();

        int diff = Math.abs(currentWaterLevel - waterRequirement);
        if (diff > 20) {
            health -= 5;
            GardenLogger.log("WARNING: " + name + " water critical. Req: " + waterRequirement + ", Curr: " + currentWaterLevel);
        }

        if (health <= 0) {
            isAlive = false;
            health = 0;
            GardenLogger.log("FATAL: " + name + " has died.");
        }
    }
    
    // Adjust water level to optimal range (within 10% of requirement)
    public void optimizeWaterLevel() {
        if (!isAlive) return;
        
        int optimalRange = waterRequirement / 10; // 10% tolerance
        int lowerBound = waterRequirement - optimalRange;
        int upperBound = waterRequirement + optimalRange;
        
        if (currentWaterLevel < lowerBound) {
            int needed = lowerBound - currentWaterLevel;
            adjustWater(needed);
            GardenLogger.log("ACTION: " + name + " water level optimized to healthy range.");
        } else if (currentWaterLevel > upperBound) {
            int excess = currentWaterLevel - upperBound;
            adjustWater(-excess);
            GardenLogger.log("ACTION: " + name + " excess water (" + excess + " units) removed to maintain health.");
        }
    }

    // Getters
    public String getName() { return name; }
    public String getType() { return type; } // Getter for type
    public int getWaterRequirement() { return waterRequirement; }
    public List<String> getVulnerableTo() { return vulnerableTo; }
    public boolean isAlive() { return isAlive; }
    public int getHealth() { return health; }
    public int getCurrentWaterLevel() { return currentWaterLevel; }

    public void heal(int amount) {
        if (isAlive) {
            this.health = Math.min(100, this.health + amount);
            // After healing, optimize water level to maintain health
            optimizeWaterLevel();
            GardenLogger.log("ACTION: " + name + " healed by " + amount + " points. Health: " + health + "%");
        }
    }

    // Manual intervention methods
    public boolean removePest() {
        if (currentPest != null && isAlive) {
            String pest = currentPest;
            currentPest = null;
            heal(15); // Recover some health after removing pest (this will also optimize water)
            GardenLogger.log("ACTION: Removed " + pest + " from " + name + ". Plant is recovering.");
            return true;
        }
        return false;
    }

    public boolean hasPest() {
        return currentPest != null && isAlive;
    }

    public String getCurrentPest() {
        return currentPest;
    }

    public void manualWater(int amount) {
        if (isAlive) {
            adjustWater(amount);
            GardenLogger.log("ACTION: Manually watered " + name + " with " + amount + " units.");
        }
    }

    public void applyFertilizer() {
        if (isAlive) {
            heal(20); // This will optimize water level
            // Fertilizer provides some water, but we'll optimize after
            adjustWater(5);
            optimizeWaterLevel(); // Ensure water stays in healthy range after fertilizer
            GardenLogger.log("ACTION: Applied fertilizer to " + name + ". Health improved!");
        }
    }

    public void emergencyTreatment() {
        if (isAlive && health < 50) {
            heal(30); // This will optimize water level
            if (currentPest != null) {
                currentPest = null;
            }
            // Ensure water level is optimal after emergency treatment
            optimizeWaterLevel();
            GardenLogger.log("ACTION: Emergency treatment applied to " + name + ". Plant condition improved significantly!");
        }
    }
}

