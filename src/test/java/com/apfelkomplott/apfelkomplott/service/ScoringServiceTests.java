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
    }

    private void addApples(GameState state, AppleLocation location, UUID containerId, int count) {
        for (int i = 0; i < count; i++) {
            Apple apple = new Apple();
            apple.setLocation(location);
            apple.setContainerId(containerId);
            state.getPlantation().getApples().add(apple);
        }
    }
}
