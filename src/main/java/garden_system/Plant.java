package garden_system;

import java.util.List;

public class Plant {
    private String name;
    private String type; // New field for icon logic
    private int waterRequirement;
    private int currentWaterLevel;
    private List<String> vulnerableTo;
    private boolean isAlive;
    private int health;

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
        checkHealth();
    }

    public void attack(String parasite) {
        if (vulnerableTo.contains(parasite)) {
            this.health -= 30;
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
        if (currentWaterLevel < 0) currentWaterLevel = 0;

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

    // Getters
    public String getName() { return name; }
    public String getType() { return type; } // Getter for type
    public int getWaterRequirement() { return waterRequirement; }
    public List<String> getVulnerableTo() { return vulnerableTo; }
    public boolean isAlive() { return isAlive; }
    public int getHealth() { return health; }
    public int getCurrentWaterLevel() { return currentWaterLevel; }

    public void heal(int amount) {
        this.health = Math.min(100, this.health + amount);
    }
}