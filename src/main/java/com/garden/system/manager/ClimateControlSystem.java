package com.garden.system.manager;

import com.garden.system.device.Cooler;
import com.garden.system.device.Heater;
import com.garden.system.device.TempSensor;
import com.garden.system.util.GardenLogger;

// Module 2: Climate Control System (Heating/Cooling)
public class ClimateControlSystem {
    private final Heater heater = new Heater();
    private final Cooler cooler = new Cooler();
    private final TempSensor sensor = new TempSensor();

    public void regulate(int currentTemp) {
        int sensed = sensor.read(currentTemp);
        GardenLogger.logEvent("INFO", "Sensor", "Temperature reading=" + sensed + "F");
        if (sensed < 50) {
            heater.on(sensed);
            cooler.off();
            GardenLogger.log("AUTOMATION: Heater activated. Warming garden.");
        } else if (sensed > 90) {
            cooler.on(sensed);
            heater.off();
            GardenLogger.log("AUTOMATION: Misting fans activated. Cooling garden.");
        } else {
            heater.off();
            cooler.off();
        }
    }

    // --- New Manual Control Methods ---

    public void turnHeaterOn() {
        heater.on(70); // Manual override acts as if temp is 70
        cooler.off();
        GardenLogger.log("MANUAL: Heater forced ON by user.");
    }

    public void turnHeaterOff() {
        heater.off();
        GardenLogger.log("MANUAL: Heater forced OFF by user.");
    }

    public void turnCoolerOn() {
        cooler.on(70); // Manual override acts as if temp is 70
        heater.off();
        GardenLogger.log("MANUAL: Cooler forced ON by user.");
    }

    public void turnCoolerOff() {
        cooler.off();
        GardenLogger.log("MANUAL: Cooler forced OFF by user.");
    }
}