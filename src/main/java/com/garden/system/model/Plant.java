package com.garden.system.model;

import com.garden.system.util.GardenLogger;

import java.util.List;

public class Plant {
    private String name;
    private String type;
    private int waterRequirement;
    private int currentWaterLevel;
    private List<String> vulnerableTo;
    private boolean isAlive;
    private int health;
    private String currentPest;

    public Plant(String name, String type, int waterRequirement, List<String> vulnerableTo) {
        this.name = name;
        this.type = type;
        this.waterRequirement = waterRequirement;
        this.vulnerableTo = vulnerableTo;
        this.currentWaterLevel = waterRequirement;
        this.isAlive = true;
        this.health = 100;
    }

    public void adjustWater(int amount) {
        this.currentWaterLevel += amount;
        checkHealth();
    }

    private void normalizeWaterLevel() {
        if (currentWaterLevel < 0) {
            currentWaterLevel = 0;
        }

        int maxWater = (int)(waterRequirement * 1.5);
        // Small plants need a buffer of at least 3 units or they oscillate too fast
        if (maxWater < 3) maxWater = 3;

        if (currentWaterLevel > maxWater) {
            int excess = currentWaterLevel - maxWater;
            currentWaterLevel = maxWater;
            GardenLogger.log("WARNING: " + name + " water level exceeded maximum (" + maxWater + "). Excess water (" + excess + " units) drained.");
        }
    }

    // --- NEW: Trait Logic ---
    public boolean isDroughtResistant() {
        // Cacti, Sunflowers, and Corn are naturally hardy against dry spells
        if (type == null) return false;
        String t = type.toLowerCase();
        return t.contains("cactus") || t.contains("sunflower") || t.contains("corn") || t.contains("succulent");
    }

    public void attack(String parasite) {
        if (vulnerableTo.contains(parasite)) {
            this.health -= 30;
            this.currentPest = parasite;
            GardenLogger.log("WARNING: " + name + " (" + type + ") attacked by " + parasite + "! Health: " + health);
        } else {
            GardenLogger.log("INFO: " + name + " is immune to " + parasite + ".");
        }
        checkHealth();
    }

    public void updateTemperatureReaction(int temp) {
        if (temp < 40 || temp > 100) {
            // Cacti love heat, don't hurt them until it's EXTREME (115+)
            if (isDroughtResistant() && temp <= 115) {
                // No damage for cactus in high heat
            } else {
                this.health -= 10;
                GardenLogger.log("WARNING: " + name + " hurting from temp (" + temp + "F).");
            }
        }
        checkHealth();
    }

    private void checkHealth() {
        normalizeWaterLevel();

        // --- UPDATED DEHYDRATION LOGIC ---
        if (currentWaterLevel == 0) {
            if (isDroughtResistant()) {
                // Cactus logic: They adapt to 0 water. No damage (or very minimal).
                // Maybe 1 damage every now and then, but effectively safe.
                GardenLogger.log("INFO: " + name + " is dry (0 Water) but using stored reserves. No damage taken.");
            } else {
                // Normal plants die fast without water
                int damage = 15;
                this.health -= damage;
                GardenLogger.log("CRITICAL: " + name + " is completely dehydrated! Taken " + damage + " damage.");
            }
        }
        else {
            int diff = Math.abs(currentWaterLevel - waterRequirement);
            // Critical variance check
            if (diff > 20) {
                this.health -= 5;
                GardenLogger.log("WARNING: " + name + " water critical. Req: " + waterRequirement + ", Curr: " + currentWaterLevel);
            }
        }

        if (health <= 0) {
            isAlive = false;
            health = 0;
            GardenLogger.log("FATAL: " + name + " has died.");
        }
    }

    public void optimizeWaterLevel() {
        if (!isAlive) return;

        int optimalRange = Math.max(1, waterRequirement / 10);
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

    // Getters and other methods remain unchanged...
    public String getName() { return name; }
    public String getType() { return type; }
    public int getWaterRequirement() { return waterRequirement; }
    public List<String> getVulnerableTo() { return vulnerableTo; }
    public boolean isAlive() { return isAlive; }
    public int getHealth() { return health; }
    public int getCurrentWaterLevel() { return currentWaterLevel; }

    public void heal(int amount) {
        if (isAlive) {
            this.health = Math.min(100, this.health + amount);
            optimizeWaterLevel();
            GardenLogger.log("ACTION: " + name + " healed by " + amount + " points. Health: " + health + "%");
        }
    }

    public boolean removePest() {
        if (currentPest != null && isAlive) {
            String pest = currentPest;
            currentPest = null;
            heal(15);
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
            heal(20);
            adjustWater(5);
            optimizeWaterLevel();
            GardenLogger.log("ACTION: Applied fertilizer to " + name + ". Health improved!");
        }
    }

    public void emergencyTreatment() {
        if (isAlive && health < 50) {
            heal(30);
            if (currentPest != null) {
                currentPest = null;
            }
            optimizeWaterLevel();
            GardenLogger.log("ACTION: Emergency treatment applied to " + name + ". Plant condition improved significantly!");
        }
    }
}