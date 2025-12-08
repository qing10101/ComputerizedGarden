package com.garden.system.manager;

import com.garden.system.device.PestTrap;
import com.garden.system.model.Plant;
import com.garden.system.util.GardenLogger;

import java.util.List;

// Module 3: Pest Defense System
public class PestDefenseSystem {
    private final PestTrap trap = new PestTrap();

    public void deployDefense(String detectedPest, List<Plant> plants) {
        boolean threatDetected = false;
        for (Plant p : plants) {
            if (p.isAlive() && p.getVulnerableTo().contains(detectedPest)) {
                threatDetected = true;
                // Heal the plant slightly as we deploy countermeasures
                p.heal(10);
            }
        }
        if (threatDetected) {
            trap.trigger(detectedPest);
            GardenLogger.log("AUTOMATION: Pesticide deployed for " + detectedPest);
        }
    }
}
