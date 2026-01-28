package com.apfelkomplott.apfelkomplott.entity;

import java.util.List;
import java.util.UUID;

public class SalesStand {

    private UUID id;
    private int capacity = 3;

    public SalesStand() {
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
                .noneMatch(a -> a.getLocation() == AppleLocation.IN_SALES_STAND
                        && id.equals(a.getContainerId()));
    }
}
