package com.apfelkomplott.apfelkomplott.engine;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.cards.ActiveProductionCard;
import com.apfelkomplott.apfelkomplott.controller.dto.SellResult;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.ScoreResult;
import com.apfelkomplott.apfelkomplott.repository.EventCardRepository;
import com.apfelkomplott.apfelkomplott.repository.ProductionCardRepository;
import com.apfelkomplott.apfelkomplott.service.DeliveryService;
import com.apfelkomplott.apfelkomplott.service.EventService;
import com.apfelkomplott.apfelkomplott.service.HarvestService;
import com.apfelkomplott.apfelkomplott.service.ProductionCardService;
import com.apfelkomplott.apfelkomplott.service.RotationService;
import com.apfelkomplott.apfelkomplott.service.ScoringService;
import com.apfelkomplott.apfelkomplott.service.SellService;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoundEngineTests {

    private static final String LONG_TERM_SCORING_CARD_ID = "LT_PRODUCTION_METHOD";

    private final RoundEngine engine = createEngine();

    @Test
    void moveMarkerTransitionsToDrawEventAndResetsRoundArtifacts() {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.MOVE_MARKER);
        state.setCurrentRound(2);
        state.setLastSellResult(new SellResult(1, 1));
        state.setLastScoreResult(new ScoreResult(1, 0, 0));
        state.setProductionCardFinalScoreResult(new ScoreResult(1, 1, 1));
        state.getPendingEventOptions().add("EVT_FUNDING");
        state.getRoundEventImpact().setHarvestLossDivisor(2);

        engine.runNextPhase(state);

        assertEquals(GamePhase.DRAW_EVENT, state.getCurrentPhase());
        assertNull(state.getLastSellResult());
        assertNull(state.getLastScoreResult());
        assertNull(state.getProductionCardFinalScoreResult());
        assertTrue(state.getPendingEventOptions().isEmpty());
        assertFalse(state.getRoundEventImpact().hasHarvestLoss());
    }

    @Test
    void moveMarkerEndsGameAtRoundFifteen() {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.MOVE_MARKER);
        state.setCurrentRound(15);

        engine.runNextPhase(state);

        assertTrue(state.isGameOver());
        assertEquals(GamePhase.MOVE_MARKER, state.getCurrentPhase());
    }

    @Test
    void drawEventPhasePreparesPendingOptionsAndStopsThere() {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.DRAW_EVENT);

        engine.runNextPhase(state);

        assertEquals(GamePhase.DRAW_EVENT, state.getCurrentPhase());
        assertEquals(2, state.getPendingEventOptions().size());
    }

    @Test
    void intermediateScoringStopsForPopupThenAdvancesOnNextCall() {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.INTERMEDIATE_SCORING);
        state.setCurrentRound(3);

        engine.runNextPhase(state);

        assertEquals(GamePhase.INTERMEDIATE_SCORING, state.getCurrentPhase());
        assertNotNull(state.getLastScoreResult());

        engine.runNextPhase(state);

        assertEquals(GamePhase.INVEST, state.getCurrentPhase());
        assertNull(state.getLastScoreResult());
    }

    @Test
    void cardScoringStopsForVisibleResultThenAdvancesRound() {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.CARD_SCORING);
        state.setCurrentRound(1);
        state.setFarmingMode(FarmingMode.CONVENTIONAL);
        state.getActiveLongTerm().add(new ActiveProductionCard(LONG_TERM_SCORING_CARD_ID, 1));

        engine.runNextPhase(state);

        assertEquals(GamePhase.CARD_SCORING, state.getCurrentPhase());
        assertNotNull(state.getProductionCardFinalScoreResult());
        assertEquals(1, state.getProductionCardFinalScoreResult().getEconomyChange());

        engine.runNextPhase(state);

        assertEquals(GamePhase.MOVE_MARKER, state.getCurrentPhase());
        assertEquals(2, state.getCurrentRound());
        assertNull(state.getProductionCardFinalScoreResult());
    }

    private RoundEngine createEngine() {
        try {
            EventService eventService = new EventService(new EventCardRepository(new ObjectMapper()));
            ProductionCardService productionCardService = new ProductionCardService(
                    new ProductionCardRepository(new ObjectMapper())
            );

            return new RoundEngine(
                    new SellService(),
                    new DeliveryService(),
                    new HarvestService(eventService),
                    new RotationService(),
                    new ScoringService(),
                    eventService,
                    productionCardService
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create RoundEngine for tests", e);
        }
    }
}
