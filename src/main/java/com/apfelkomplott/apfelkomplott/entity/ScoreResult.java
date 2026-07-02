package com.apfelkomplott.apfelkomplott.entity;

import java.util.ArrayList;
import java.util.List;

public class ScoreResult {

    private int economyChange;
    private int environmentChange;
    private int healthChange;
    private int wastedApples;
    private int applesProduced;
    private int transportCapacity;
    private String wasteReason;

    private List<String> reasons = new ArrayList<>();

    public ScoreResult(int economyChange, int environmentChange, int healthChange) {
        this.economyChange = economyChange;
        this.environmentChange = environmentChange;
        this.healthChange = healthChange;
    }

    public void addReason(String reason) {
        reasons.add(reason);
    }

    public List<String> getReasons() {
        return reasons;
    }

    public int getEconomyChange() { return economyChange; }
    public int getEnvironmentChange() { return environmentChange; }
    public int getHealthChange() { return healthChange; }
    public int getWastedApples() { return wastedApples; }
    public int getApplesProduced() { return applesProduced; }
    public int getTransportCapacity() { return transportCapacity; }
    public String getWasteReason() { return wasteReason; }

    public void setEconomyChange(int economyChange) {
        this.economyChange = economyChange;
    }

    public void setEnvironmentChange(int environmentChange) {
        this.environmentChange = environmentChange;
    }

    public void setHealthChange(int healthChange) {
        this.healthChange = healthChange;
    }

    public void setWastedApples(int wastedApples) {
        this.wastedApples = wastedApples;
    }

    public void setApplesProduced(int applesProduced) {
        this.applesProduced = applesProduced;
    }

    public void setTransportCapacity(int transportCapacity) {
        this.transportCapacity = transportCapacity;
    }

    public void setWasteReason(String wasteReason) {
        this.wasteReason = wasteReason;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }
}
