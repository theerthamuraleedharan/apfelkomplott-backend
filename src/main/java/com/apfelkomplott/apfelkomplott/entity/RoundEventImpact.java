package com.apfelkomplott.apfelkomplott.entity;

import com.apfelkomplott.apfelkomplott.Enum.PlantationSize;

import java.util.EnumMap;
import java.util.Map;

public class RoundEventImpact {

    private final Map<PlantationSize, Integer> harvestLossByPlantationSize = new EnumMap<>(PlantationSize.class);
    private Integer harvestLossDivisor;

    public Map<PlantationSize, Integer> getHarvestLossByPlantationSize() {
        return harvestLossByPlantationSize;
    }

    public Integer getHarvestLossDivisor() {
        return harvestLossDivisor;
    }

    public void setHarvestLossDivisor(Integer harvestLossDivisor) {
        this.harvestLossDivisor = harvestLossDivisor;
    }

    public boolean hasHarvestLoss() {
        return harvestLossDivisor != null
                && harvestLossDivisor > 0
                && !harvestLossByPlantationSize.isEmpty();
    }

    public void clear() {
        harvestLossByPlantationSize.clear();
        harvestLossDivisor = null;
    }
}
