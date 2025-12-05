package garden_system;

import java.util.List;

public class Plant {
    private String name;
    private int waterRequirement; // Ideal water level
    private int currentWaterLevel;
    private List<String> vulnerableTo;
    private boolean isAlive;
    private int health; // 0 to 100

    public Plant(String name, int waterRequirement, List<String> vulnerableTo) {
        this.name = name;
        this.waterRequirement = waterRequirement;
        this.vulnerableTo = vulnerableTo;
        this.currentWaterLevel = waterRequirement; // Start healthy
        this.isAlive = true;
        this.health = 100;
    }

    public void adjustWater(int amount) {
        this.currentWaterLevel += amount;
        checkHealth();
    }

    public void attack(String parasite) {
        if (vulnerableTo.contains(parasite)) {
            this.health -= 30;
            GardenLogger.log("WARNING: " + name + " was attacked by " + parasite + "! Health is now " + health);
        } else {
            GardenLogger.log("INFO: " + name + " is immune to " + parasite + ".");
        }
        checkHealth();
    }

    public void updateTemperatureReaction(int temp) {
        // Simple logic: Extreme temps hurt the plant
        if (temp < 40 || temp > 100) {
            this.health -= 10;
            GardenLogger.log("WARNING: " + name + " is suffering from extreme temperature (" + temp + "F).");
        }
        checkHealth();
    }

    private void checkHealth() {
        // Water Logic
        if (currentWaterLevel < 0) currentWaterLevel = 0;

        // If water is way off target (+/- 20), health drops
        int diff = Math.abs(currentWaterLevel - waterRequirement);
        if (diff > 20) {
            health -= 5;
            GardenLogger.log("WARNING: " + name + " water levels critical. Req: " + waterRequirement + ", Curr: " + currentWaterLevel);
        }

        if (health <= 0) {
            isAlive = false;
            health = 0;
            GardenLogger.log("FATAL: " + name + " has died.");
        }
    }

    // Getters
    public String getName() { return name; }
    public int getWaterRequirement() { return waterRequirement; }
    public List<String> getVulnerableTo() { return vulnerableTo; }
    public boolean isAlive() { return isAlive; }
    public int getHealth() { return health; }
    public int getCurrentWaterLevel() { return currentWaterLevel; }

    // Healing logic for the automated system
    public void heal(int amount) {
        this.health = Math.min(100, this.health + amount);
    }
}
