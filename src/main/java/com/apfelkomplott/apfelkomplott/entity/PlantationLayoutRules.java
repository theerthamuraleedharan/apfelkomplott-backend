package com.apfelkomplott.apfelkomplott.entity;

public final class PlantationLayoutRules {

    public static final int MAX_FIELD_CAPACITY = 8;

    private PlantationLayoutRules() {
    }

    public static PlantationSize classify(int treesInArea) {
        if (treesInArea <= 0) {
            throw new IllegalArgumentException("Plantation area size must be positive.");
        }
        if (treesInArea <= 2) {
            return PlantationSize.SMALL;
        }
        if (treesInArea <= 4) {
            return PlantationSize.MEDIUM;
        }
        return PlantationSize.LARGE;
    }
}
