package com.garden.system.manager;

import com.garden.system.model.Plant;
import com.garden.system.util.GardenLogger;

import java.util.List;

// Module 1: Hydration System
public class HydrationSystem {
    public void regulate(List<Plant> plants) {
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
                GardenLogger.log("AUTOMATION: Sprinklers activated for " + p.getName() + " (+" + needed + " units)");
                p.adjustWater(needed);
            } else if (currentWater > upperBound) {
                // Too much water - drain excess
                int excess = currentWater - upperBound;
                int drainAmount = Math.min(5, excess);
                GardenLogger.log("AUTOMATION: Drainage opened for " + p.getName() + " (-" + drainAmount + " units)");
                p.adjustWater(-drainAmount);
            }
            // If water is in optimal range, no action needed
        }
    }
}

