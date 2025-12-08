package com.garden.system.manager;

import com.garden.system.device.MoistureSensor;
import com.garden.system.device.Sprinkler;
import com.garden.system.model.Plant;
import com.garden.system.util.GardenLogger;

import java.util.List;

// Module 1: Hydration System
public class HydrationSystem {
    private final Sprinkler sprinkler = new Sprinkler();
    private final MoistureSensor sensor = new MoistureSensor();

    public void regulate(List<Plant> plants) {
        int avgMoisture = sensor.readAverageMoisture(plants);
        GardenLogger.logEvent("INFO", "Sensor", "Moisture average=" + avgMoisture);
        for (Plant p : plants) {
            if (!p.isAlive()) continue;

            int currentWater = p.getCurrentWaterLevel();
            int requirement = p.getWaterRequirement();
            int maxWater = (int)(requirement * 1.5); // Maximum water level (150% of requirement)
            int optimalRange = requirement / 10; // 10% tolerance
            int lowerBound = requirement - optimalRange;
            int upperBound = requirement + optimalRange;

            if (currentWater < lowerBound) {
                // Too little water
                int needed = Math.min(5, lowerBound - currentWater);
                sprinkler.activate(p.getName(), needed);
                p.adjustWater(needed);
            } else if (currentWater > upperBound) {
                // Too much water - drain excess
                int excess = currentWater - upperBound;
                int drainAmount = Math.min(5, excess);
                GardenLogger.log("AUTOMATION: Drainage opened for " + p.getName() + " (-" + drainAmount + " units)");
                p.adjustWater(-drainAmount);
                sprinkler.deactivate();
            } else {
                sprinkler.deactivate();
            }
            // If water is in optimal range, no action needed
        }
    }
}
