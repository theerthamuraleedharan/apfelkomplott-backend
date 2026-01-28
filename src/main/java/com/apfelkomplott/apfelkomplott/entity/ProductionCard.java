package com.apfelkomplott.apfelkomplott.entity;

public interface ProductionCard {

    String getName();

    void apply(GameState state);

    boolean isOngoing();
}
