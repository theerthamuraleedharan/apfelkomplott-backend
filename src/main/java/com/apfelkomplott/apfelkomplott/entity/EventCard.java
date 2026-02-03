package com.apfelkomplott.apfelkomplott.entity;

public interface EventCard {

    String getName();

    // Called immediately when drawn
    void onDraw(GameState state);

    // Applied every round while active
    void apply(GameState state);

    // Should it apply in the same round?
    boolean isImmediate();
}
