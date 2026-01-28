package com.apfelkomplott.apfelkomplott.entity;

import java.util.List;
import java.util.UUID;

public class Crate {
    public Crate(UUID id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    private UUID id;
    private int capacity = 3;

    public Crate() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isEmpty(List<Apple> apples) {
        return apples.stream()
                .noneMatch(a -> a.getLocation() == AppleLocation.IN_CRATE
                        && id.equals(a.getContainerId()));
    }
}

