package com.apfelkomplott.apfelkomplott.entity;

import java.util.ArrayList;
import java.util.List;

public class Plantation {

    private List<Tree> trees = new ArrayList<>();
    private List<Apple> apples = new ArrayList<>();
    private List<Crate> crates = new ArrayList<>();
    private List<SalesStand> salesStands = new ArrayList<>();

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

    public void rotate() {
        for (Tree tree : trees) {
            tree.setFieldPosition(tree.getFieldPosition() + 1);
        }
    }
}
