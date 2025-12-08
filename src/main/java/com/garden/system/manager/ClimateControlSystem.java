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
    
    // Optimal temperature range: 40-100F (plants get hurt outside this range)
    private static final int MIN_SAFE_TEMP = 40;
    private static final int MAX_SAFE_TEMP = 100;
    private static final int TARGET_MIN_TEMP = 50; // Start heating below this
    private static final int TARGET_MAX_TEMP = 90; // Start cooling above this

    public void regulate(int currentTemp) {
        int sensed = sensor.read(currentTemp);
        GardenLogger.logEvent("INFO", "Sensor", "Temperature reading=" + sensed + "F");
        
        // Adjust thresholds to match plant safety range (40-100F)
        if (sensed < TARGET_MIN_TEMP) {
            heater.on(sensed);
            cooler.off();
            GardenLogger.log("AUTOMATION: Heater activated. Warming garden from " + sensed + "F.");
        } else if (sensed > TARGET_MAX_TEMP) {
            cooler.on(sensed);
            heater.off();
            GardenLogger.log("AUTOMATION: Misting fans activated. Cooling garden from " + sensed + "F.");
        } else {
            heater.off();
            cooler.off();
        }
    }

    /**
     * Calculate effective temperature after applying heating/cooling effects
     * Simulates gradual temperature change when devices are active
     */
    public int getEffectiveTemperature(int currentTemp) {
        int effectiveTemp = currentTemp;
        
        if (heater.isOn()) {
            // Heater gradually increases temperature towards safe range
            // Target: around 60-70F (middle of safe range)
            int targetTemp = 65;
            if (effectiveTemp < targetTemp) {
                // Increase by up to 5 degrees per cycle, but don't exceed target
                effectiveTemp = Math.min(targetTemp, effectiveTemp + 5);
                if (effectiveTemp != currentTemp) {
                    GardenLogger.log("AUTOMATION: Temperature rising to " + effectiveTemp + "F (heater active).");
                }
            }
        }
        
        if (cooler.isOn()) {
            // Cooler gradually decreases temperature towards safe range
            // Target: around 60-70F (middle of safe range)
            int targetTemp = 65;
            if (effectiveTemp > targetTemp) {
                // Decrease by up to 5 degrees per cycle, but don't go below target
                effectiveTemp = Math.max(targetTemp, effectiveTemp - 5);
                if (effectiveTemp != currentTemp) {
                    GardenLogger.log("AUTOMATION: Temperature dropping to " + effectiveTemp + "F (cooler active).");
                }
            }
        }
        
        // Ensure temperature stays within absolute safe bounds
        if (effectiveTemp < MIN_SAFE_TEMP) {
            effectiveTemp = MIN_SAFE_TEMP;
        } else if (effectiveTemp > MAX_SAFE_TEMP) {
            effectiveTemp = MAX_SAFE_TEMP;
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