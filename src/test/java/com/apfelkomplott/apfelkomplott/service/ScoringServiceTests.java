package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.Apple;
import com.apfelkomplott.apfelkomplott.entity.AppleLocation;
import com.apfelkomplott.apfelkomplott.entity.Crate;
import com.apfelkomplott.apfelkomplott.entity.GameResult;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.SalesStand;
import com.apfelkomplott.apfelkomplott.entity.ScoreResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScoringServiceTests {

    private final ScoringService service = new ScoringService();

    @Test
    void intermediateScoringPenalizesWastedApplesAndRemovesThem() {
        GameState state = new GameState();
        state.setCurrentRound(3);
        addApples(state, AppleLocation.WASTED, null, 3);

        ScoreResult result = service.applyIntermediateScoring(state);

        assertEquals(-1, result.getEconomyChange());
        assertEquals(-1, state.getScoreTrack().getEconomy());
        assertTrue(result.getReasons().contains("-1 Economy (Wasted apples)"));
        assertEquals(3, result.getWastedApples());
        assertEquals(3, result.getApplesProduced());
        assertEquals(0, result.getTransportCapacity());
        assertEquals(
                "3 apples were wasted because no transport capacity was available.",
                result.getWasteReason());
        assertTrue(state.getPlantation().getApples().isEmpty());
    }

    @Test
    void intermediateScoringPenalizesEmptyLogisticsAndCanEndGame() {
        GameState state = new GameState();
        state.setCurrentRound(3);
        state.getScoreTrack().setEconomy(-1);
        state.getPlantation().getCrates().add(new Crate(UUID.randomUUID(), 3));
        state.getPlantation().getSalesStands().add(new SalesStand());

        ScoreResult result = service.applyIntermediateScoring(state);

        assertEquals(-2, result.getEconomyChange());
        assertEquals(-3, state.getScoreTrack().getEconomy());
        assertTrue(state.isGameOver());
        assertEquals(GameResult.LOSS, state.getGameResult());
    }

    @Test
    void intermediateScoringAwardsPerfectBalanceBonus() {
        GameState state = new GameState();
        state.setCurrentRound(3);
        Crate crate = new Crate(UUID.randomUUID(), 3);
        SalesStand stand = new SalesStand();
        state.getPlantation().getCrates().add(crate);
        state.getPlantation().getSalesStands().add(stand);
        addApples(state, AppleLocation.IN_TRANSPORT, crate.getId(), 3);
        addApples(state, AppleLocation.IN_SALES_STAND, stand.getId(), 3);

        ScoreResult result = service.applyIntermediateScoring(state);

        assertEquals(1, result.getEconomyChange());
        assertEquals(1, state.getScoreTrack().getEconomy());
        assertFalse(state.isGameOver());
        assertTrue(result.getReasons().contains("+1 Economy (Perfect balance bonus)"));
        assertEquals(0, result.getWastedApples());
        assertEquals(3, result.getApplesProduced());
        assertEquals(3, result.getTransportCapacity());
        assertEquals(
                "No apples were wasted because the available transport capacity was sufficient.",
                result.getWasteReason());
    }

    @Test
    void intermediateScoringExplainsWasteWhenProductionExceedsTransportCapacity() {
        GameState state = new GameState();
        state.setCurrentRound(3);
        Crate crate = new Crate(UUID.randomUUID(), 3);
        state.getPlantation().getCrates().add(crate);
        addApples(state, AppleLocation.IN_TRANSPORT, crate.getId(), 3);
        addApples(state, AppleLocation.WASTED, null, 2);

        ScoreResult result = service.applyIntermediateScoring(state);

        assertEquals(2, result.getWastedApples());
        assertEquals(5, result.getApplesProduced());
        assertEquals(3, result.getTransportCapacity());
        assertEquals(
                "2 apples were wasted because 5 apples were produced, but only 3 transport spaces were available.",
                result.getWasteReason());
    }

    private void addApples(GameState state, AppleLocation location, UUID containerId, int count) {
        for (int i = 0; i < count; i++) {
            Apple apple = new Apple();
            apple.setLocation(location);
            apple.setContainerId(containerId);
            apple.setHarvestedRound(state.getCurrentRound());
            state.getPlantation().getApples().add(apple);
        }
    }
}
