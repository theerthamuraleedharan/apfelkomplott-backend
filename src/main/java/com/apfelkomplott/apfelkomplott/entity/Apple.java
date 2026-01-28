package com.apfelkomplott.apfelkomplott.entity;

import java.util.UUID;

public class Apple {

    public Apple() {
        this.id = UUID.randomUUID();
    }

    private UUID id;

    private int harvestedRound;

    private AppleLocation location;

    private UUID containerId; // Crate ID or Stand ID

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getHarvestedRound() {
        return harvestedRound;
    }

    public void setHarvestedRound(int harvestedRound) {
        this.harvestedRound = harvestedRound;
    }

    public AppleLocation getLocation() {
        return location;
    }

    public void setLocation(AppleLocation location) {
        this.location = location;
    }

    public UUID getContainerId() {
        return containerId;
    }

    public void setContainerId(UUID containerId) {
        this.containerId = containerId;
    }
}
