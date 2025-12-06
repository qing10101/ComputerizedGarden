package com.garden.system.manager;

import com.garden.system.model.Plant;
import com.garden.system.util.GardenLogger;

import java.util.List;

// Module 3: Pest Defense System
public class PestDefenseSystem {
    public void deployDefense(String detectedPest, List<Plant> plants) {
        boolean threadDetected = false;
        for(Plant p : plants) {
            if(p.isAlive() && p.getVulnerableTo().contains(detectedPest)) {
                threadDetected = true;
                // Heal the plant slightly as we deploy countermeasures
                p.heal(10);
            }
        }
        if(threadDetected) {
            GardenLogger.log("AUTOMATION: Pesticide deployed for " + detectedPest);
        }
    }
}

