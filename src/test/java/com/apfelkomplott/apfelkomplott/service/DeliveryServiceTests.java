package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.Apple;
import com.apfelkomplott.apfelkomplott.entity.AppleLocation;
import com.apfelkomplott.apfelkomplott.entity.Crate;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.SalesStand;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeliveryServiceTests {

    private final DeliveryService service = new DeliveryService();

    @Test
    void deliverMovesTransportApplesIntoSalesStandUntilCapacityIsFull() {
        GameState state = new GameState();
        Crate crate = new Crate(UUID.randomUUID(), 4);
        SalesStand stand = new SalesStand();
        state.getPlantation().getCrates().add(crate);
        state.getPlantation().getSalesStands().add(stand);
        addTransportApples(state, crate, 4);

        service.deliver(state);

        assertEquals(3, countApples(state, AppleLocation.IN_SALES_STAND));
        assertEquals(1, countApples(state, AppleLocation.IN_TRANSPORT));
        assertEquals(stand.getId(), state.getPlantation().getApples().get(0).getContainerId());
    }

    @Test
    void deliverContinuesIntoNextSalesStandWhenAvailable() {
        GameState state = new GameState();
        Crate crate = new Crate(UUID.randomUUID(), 4);
        state.getPlantation().getCrates().add(crate);
        state.getPlantation().getSalesStands().add(new SalesStand());
        state.getPlantation().getSalesStands().add(new SalesStand());
        addTransportApples(state, crate, 4);

        service.deliver(state);

        assertEquals(4, countApples(state, AppleLocation.IN_SALES_STAND));
        assertEquals(0, countApples(state, AppleLocation.IN_TRANSPORT));
    }

    private void addTransportApples(GameState state, Crate crate, int count) {
        for (int i = 0; i < count; i++) {
            Apple apple = new Apple();
            apple.setLocation(AppleLocation.IN_TRANSPORT);
            apple.setContainerId(crate.getId());
            state.getPlantation().getApples().add(apple);
        }
    }

    private long countApples(GameState state, AppleLocation location) {
        return state.getPlantation().getApples().stream()
                .filter(apple -> apple.getLocation() == location)
                .count();
    }
}
