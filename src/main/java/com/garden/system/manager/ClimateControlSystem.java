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
    
    // Temperature thresholds: heat below 50F, cool above 90F
    private static final int MIN_TEMP = 50; // Heat when below this
    private static final int MAX_TEMP = 90; // Cool when above this

    public void regulate(int currentTemp) {
        int sensed = sensor.read(currentTemp);
        GardenLogger.logEvent("INFO", "Sensor", "Temperature reading=" + sensed + "F");
        
        if (sensed < MIN_TEMP) {
            heater.on(sensed);
            cooler.off();
            GardenLogger.log("AUTOMATION: Heater activated. Temperature is " + sensed + "F, warming to above " + MIN_TEMP + "F.");
        } else if (sensed > MAX_TEMP) {
            cooler.on(sensed);
            heater.off();
            GardenLogger.log("AUTOMATION: Cooler activated. Temperature is " + sensed + "F, cooling to below " + MAX_TEMP + "F.");
        } else {
            heater.off();
            cooler.off();
            GardenLogger.log("AUTOMATION: Temperature is " + sensed + "F (within safe range " + MIN_TEMP + "-" + MAX_TEMP + "F). No climate control needed.");
        }
    }

    /**
     * Calculate effective temperature after applying heating/cooling effects
     * Directly adjusts temperature to safe range (50-90F)
     */
    public int getEffectiveTemperature(int currentTemp) {
        int effectiveTemp = currentTemp;
        
        if (heater.isOn()) {
            // Heater directly increases temperature to above 50F
            if (effectiveTemp < MIN_TEMP) {
                int targetTemp = MIN_TEMP + 1; // Set to 51F (above 50F)
                effectiveTemp = targetTemp;
                GardenLogger.log("AUTOMATION: Temperature adjusted from " + currentTemp + "F to " + effectiveTemp + "F (heater active, now above " + MIN_TEMP + "F).");
            }
        }
        
        if (cooler.isOn()) {
            // Cooler directly decreases temperature to below 90F
            if (effectiveTemp > MAX_TEMP) {
                int targetTemp = MAX_TEMP - 1; // Set to 89F (below 90F)
                effectiveTemp = targetTemp;
                GardenLogger.log("AUTOMATION: Temperature adjusted from " + currentTemp + "F to " + effectiveTemp + "F (cooler active, now below " + MAX_TEMP + "F).");
            }
        }
        
        return effectiveTemp;
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