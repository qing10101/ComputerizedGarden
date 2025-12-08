package com.garden.system.device;

import com.garden.system.util.GardenLogger;

public class PestTrap {
    public void trigger(String pest) {
        GardenLogger.logEvent("INFO", "Device", "PestTrap triggered for pest=" + pest);
    }
}
