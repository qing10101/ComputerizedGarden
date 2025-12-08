package com.garden.system.util;

import com.garden.system.manager.GardenManager;
import com.garden.system.model.Plant;

/**
 * Simple monitoring hook to track long-running stability.
 * For now it just logs a heartbeat; can be swapped with TA-provided API later.
 */
public class MonitoringService {
    public interface MonitorClient {
        void onHeartbeat(int dayCount, int totalPlants, long alivePlants);
    }

    private MonitorClient client;

    public void registerMonitorClient(MonitorClient client) {
        this.client = client;
        GardenLogger.logEvent("INFO", "Monitor", "External monitor registered");
    }

    public void heartbeat(int dayCount) {
        GardenManager manager = GardenManager.getInstance();
        int plants = manager.getPlants().size();
        long alive = manager.getPlants().stream().filter(Plant::isAlive).count();
        GardenLogger.logEvent("INFO", "Monitor",
                String.format("Heartbeat day=%d plants=%d alive=%d", dayCount, plants, alive));
        if (client != null) {
            try {
                client.onHeartbeat(dayCount, plants, alive);
            } catch (Exception e) {
                GardenLogger.logEvent("ERROR", "Monitor", "External monitor failed: " + e.getMessage());
            }
        }
    }
}
