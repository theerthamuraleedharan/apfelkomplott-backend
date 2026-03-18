package com.apfelkomplott.apfelkomplott.cards;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.Enum.PlantationSize;

import java.util.EnumMap;
import java.util.Map;

public class EventEffectDef {

    private EventEffectType type;
    private Integer amount;
    private Integer divisor;
    private Map<FarmingMode, Integer> amountByFarmingMode = new EnumMap<>(FarmingMode.class);
    private Map<PlantationSize, Integer> amountByPlantationSize = new EnumMap<>(PlantationSize.class);

    public EventEffectDef() {
    }

    public EventEffectDef(EventEffectType type, Integer amount) {
        this.type = type;
        this.amount = amount;
    }

    public EventEffectDef(
            EventEffectType type,
            Integer divisor,
            Map<PlantationSize, Integer> amountByPlantationSize
    ) {
        this.type = type;
        this.divisor = divisor;
        this.amountByPlantationSize = amountByPlantationSize;
    }

    public EventEffectType getType() {
        return type;
    }

    public void setType(EventEffectType type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getDivisor() {
        return divisor;
    }

    public void setDivisor(Integer divisor) {
        this.divisor = divisor;
    }

    public Map<FarmingMode, Integer> getAmountByFarmingMode() {
        return amountByFarmingMode;
    }

    public void setAmountByFarmingMode(Map<FarmingMode, Integer> amountByFarmingMode) {
        this.amountByFarmingMode = amountByFarmingMode;
    }

    public Map<PlantationSize, Integer> getAmountByPlantationSize() {
        return amountByPlantationSize;
    }

    public void setAmountByPlantationSize(Map<PlantationSize, Integer> amountByPlantationSize) {
        this.amountByPlantationSize = amountByPlantationSize;
    }
}
