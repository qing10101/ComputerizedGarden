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

            if (currentWater < requirement) {
                // Too little water - directly adjust to ideal value
                int needed = requirement - currentWater;
                GardenLogger.log("AUTOMATION: Sprinkler activated for " + p.getName() + ". Water adjusted from " + currentWater + " to " + requirement + " (+" + needed + " units).");
                sprinkler.activate(p.getName(), needed);
                p.adjustWater(needed);
            } else if (currentWater > requirement) {
                // Too much water - directly adjust to ideal value
                int excess = currentWater - requirement;
                GardenLogger.log("AUTOMATION: Drainage opened for " + p.getName() + ". Water adjusted from " + currentWater + " to " + requirement + " (-" + excess + " units).");
                p.adjustWater(-excess);
                sprinkler.deactivate();
            } else {
                // Water is at ideal value
                sprinkler.deactivate();
            }
        }
    }
}
