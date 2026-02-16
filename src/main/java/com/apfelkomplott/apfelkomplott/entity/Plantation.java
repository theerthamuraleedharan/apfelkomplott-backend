package com.apfelkomplott.apfelkomplott.entity;

import java.util.ArrayList;
import java.util.List;

public class Plantation {

    private List<Tree> trees = new ArrayList<>();
    private List<Apple> apples = new ArrayList<>();
    private List<Crate> crates = new ArrayList<>();
    private List<SalesStand> salesStands = new ArrayList<>();

    private int applePriceModifier = 0;

    private int discardedApples = 0;

    public void incrementDiscardedApples() {
        discardedApples++;
    }

    public int getDiscardedApples() {
        return discardedApples;
    }

    public void resetDiscardedApples() {
        discardedApples = 0;
    }

    public List<Tree> getTrees() {
        return trees;
    }

    public List<Apple> getApples() {
        return apples;
    }

    public List<Crate> getCrates() {
        return crates;
    }

    public List<SalesStand> getSalesStands() {
        return salesStands;
    }

    public void setTrees(List<Tree> trees) {
        this.trees = trees;
    }

    public void setApples(List<Apple> apples) {
        this.apples = apples;
    }

    public void setCrates(List<Crate> crates) {
        this.crates = crates;
    }

    public void setSalesStands(List<SalesStand> salesStands) {
        this.salesStands = salesStands;
    }

    public int getApplePriceModifier() {
        return applePriceModifier;
    }

    public void setApplePriceModifier(int applePriceModifier) {
        this.applePriceModifier = applePriceModifier;
    }

    public void resetApplePriceModifier() {
        this.applePriceModifier = 0;
    }

    public void rotate() {
        for (Tree tree : trees) {
            tree.setFieldPosition(tree.getFieldPosition() + 1);
        }
    }
}
