package com.apfelkomplott.apfelkomplott.cards;

import lombok.Data;
import java.util.List;

@Data
public class EffectDef {

    private List<Integer> years;

    private int economy;
    private int environment;
    private int health;

   public boolean appliesInYear(int year) {
    return years != null && years.contains(year);
    }


    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
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
