package com.garden.system.manager;

import com.garden.system.util.GardenLogger;

// Module 2: Climate Control System (Heating/Cooling)
public class ClimateControlSystem {
    public void regulate(int currentTemp) {
        if (currentTemp < 50) {
            GardenLogger.log("AUTOMATION: Heater activated. Warming garden.");
        } else if (currentTemp > 90) {
            GardenLogger.log("AUTOMATION: Misting fans activated. Cooling garden.");
        }
    }
}

