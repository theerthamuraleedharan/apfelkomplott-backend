package com.apfelkomplott.apfelkomplott.entity;

import java.util.ArrayList;
import java.util.List;

public class ScoreResult {

    private int economyChange;
    private int environmentChange;
    private int healthChange;

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

    public void setEconomyChange(int economyChange) {
        this.economyChange = economyChange;
    }

    public void setEnvironmentChange(int environmentChange) {
        this.environmentChange = environmentChange;
    }

    public void setHealthChange(int healthChange) {
        this.healthChange = healthChange;
    }

    public void setReasons(List<String> reasons) {
        this.reasons = reasons;
    }
}
