package com.garden.system.device;

import com.garden.system.util.GardenLogger;

public class Cooler {
    private boolean on;

    public void on(int temp) {
        if (!on) {
            GardenLogger.logEvent("INFO", "Device", "Cooler ON temp=" + temp + "F");
        }
        on = true;
    }

    public void off() {
        if (on) {
            GardenLogger.logEvent("INFO", "Device", "Cooler OFF");
        }
        on = false;
    }

    public boolean isOn() { return on; }
}
