package com.apfelkomplott.apfelkomplott.entity;

public class ScoreTrack {

    private int economy = 0;
    private int environment = 0;
    private int health = 0;

    public boolean isGameOver() {
        return economy <= -3 || environment <= -3 || health <= -3;
    }

    public int getEconomy() {
        return economy;
    }

    public void setEconomy(int economy) {
        this.economy = economy;
    }

    public int getEnvironment() {
        return environment;
    }

    public void setEnvironment(int environment) {
        this.environment = environment;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
