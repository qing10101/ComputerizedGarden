package com.garden.system.device;

import com.garden.system.util.GardenLogger;

public class Sprinkler {
    private boolean on;
    private int flowLevel;

    public void activate(String target, int level) {
        on = true;
        flowLevel = level;
        GardenLogger.logEvent("INFO", "Device", String.format("Sprinkler ON for %s (+%d units)", target, level));
    }

    public void deactivate() {
        if (on) {
            GardenLogger.logEvent("INFO", "Device", "Sprinkler OFF");
        }
        on = false;
        flowLevel = 0;
    }

    public boolean isOn() { return on; }
    public int getFlowLevel() { return flowLevel; }
}
