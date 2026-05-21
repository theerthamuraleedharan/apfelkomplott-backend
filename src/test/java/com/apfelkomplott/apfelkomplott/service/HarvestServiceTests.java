package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.AppleLocation;
import com.apfelkomplott.apfelkomplott.entity.Crate;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.Tree;
import com.apfelkomplott.apfelkomplott.entity.TreeType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HarvestServiceTests {

    private final EventService eventService = mock(EventService.class);
    private final HarvestService service = new HarvestService(eventService);

    @Test
    void harvestPlacesMatureApplesIntoTransportAndWastesOverflow() {
        GameState state = new GameState();
        state.setCurrentRound(4);
        Crate crate = new Crate(UUID.randomUUID(), 2);
        state.getPlantation().getCrates().add(crate);
        addTrees(state, 3, 3);
        addTrees(state, 2, 1);
        when(eventService.calculateHarvestLoss(state)).thenReturn(0);

        service.harvest(state);

        assertEquals(3, state.getPlantation().getApples().size());
        assertEquals(2, countApples(state, AppleLocation.IN_TRANSPORT));
        assertEquals(1, countApples(state, AppleLocation.WASTED));
        assertEquals(4, state.getPlantation().getApples().get(0).getHarvestedRound());
        assertEquals(crate.getId(), state.getPlantation().getApples().get(0).getContainerId());
    }

    @Test
    void harvestAppliesEventLossAndClearsRoundImpact() {
        GameState state = new GameState();
        state.getPlantation().getCrates().add(new Crate(UUID.randomUUID(), 10));
        state.getRoundEventImpact().setHarvestLossDivisor(2);
        addTrees(state, 3, 5);
        when(eventService.calculateHarvestLoss(state)).thenReturn(2);

        service.harvest(state);

        assertEquals(3, state.getPlantation().getApples().size());
        assertEquals(3, countApples(state, AppleLocation.IN_TRANSPORT));
        assertFalse(state.getRoundEventImpact().hasHarvestLoss());
    }

    private void addTrees(GameState state, int fieldPosition, int count) {
        for (int i = 0; i < count; i++) {
            Tree tree = new Tree();
            tree.setType(TreeType.SEEDLING);
            tree.setFieldPosition(fieldPosition);
            state.getPlantation().getTrees().add(tree);
        }
    }

    private long countApples(GameState state, AppleLocation location) {
        return state.getPlantation().getApples().stream()
                .filter(apple -> apple.getLocation() == location)
                .count();
    }
}
