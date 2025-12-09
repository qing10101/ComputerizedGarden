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
        // sensor.readAverageMoisture(plants); // Optional logging

        for (Plant p : plants) {
            if (!p.isAlive()) continue;

            int currentWater = p.getCurrentWaterLevel();
            int requirement = p.getWaterRequirement();

            int tolerance = (int) (requirement * 0.4);
            if (tolerance < 2) tolerance = 2;

            int lowerBound = requirement - tolerance;

            // --- FIX FOR SMALL PLANTS (Cactus) ---
            // If requirement is small (e.g. 2), lowerBound might be <= 0.
            // If lowerBound is <= 0, the sprinkler never triggers because water can't go below 0.
            // We force a minimum trigger point of 1 for any plant that needs water.
            if (lowerBound < 1 && requirement > 0) {
                lowerBound = 1;
            }

            int upperBound = requirement + tolerance;

            if (currentWater < lowerBound) {
                // Activate Sprinklers
                int needed = requirement - currentWater;
                int flow = Math.min(5, needed);

                sprinkler.activate(p.getName(), flow);
                p.adjustWater(flow);

            } else if (currentWater > upperBound) {
                // Open Drainage
                int excess = currentWater - upperBound;
                int drainAmount = Math.min(5, excess);

                GardenLogger.log("AUTOMATION: Drainage opened for " + p.getName() + " (-" + drainAmount + " units)");
                p.adjustWater(-drainAmount);
                sprinkler.deactivate();

            } else {
                sprinkler.deactivate();
            }
        }
    }
}