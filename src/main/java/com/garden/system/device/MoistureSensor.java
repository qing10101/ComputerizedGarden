package com.garden.system.device;

import com.garden.system.model.Plant;

import java.util.List;

public class MoistureSensor {
    public int readAverageMoisture(List<Plant> plants) {
        if (plants == null || plants.isEmpty()) return 0;
        int sum = 0;
        int count = 0;
        for (Plant p : plants) {
            sum += p.getCurrentWaterLevel();
            count++;
        }
        return count == 0 ? 0 : sum / count;
    }
}
