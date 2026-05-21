package com.apfelkomplott.apfelkomplott;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.Tree;
import com.apfelkomplott.apfelkomplott.entity.TreeType;
import com.apfelkomplott.apfelkomplott.service.GameStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:5173")
class GameIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameStateService gameStateService;

    @BeforeEach
    void resetState() {
        gameStateService.createNewGame(null);
    }

    @Test
    void startGameInitializesStateAndMarket() throws Exception {
        mockMvc.perform(post("/game/start").param("mode", FarmingMode.CONVENTIONAL.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.farmingMode").value("CONVENTIONAL"))
                .andExpect(jsonPath("$.currentRound").value(1))
                .andExpect(jsonPath("$.currentPhase").value("MOVE_MARKER"))
                .andExpect(jsonPath("$.money").value(60))
                .andExpect(jsonPath("$.gameOver").value(false))
                .andExpect(jsonPath("$.gameResult").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.marketCardIds.length()").value(5))
                .andExpect(jsonPath("$.eventDrawPile.length()").isNotEmpty());

        mockMvc.perform(get("/game/state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameResult").value("IN_PROGRESS"));
    }

    @Test
    void investEndpointAllowsSeedlingWhenOnlyOtherFieldsAreFull() throws Exception {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.INVEST);
        state.setMoney(10);
        addTrees(state, 1, 7);
        addTrees(state, 2, 8);
        gameStateService.createNewGame(state);

        mockMvc.perform(post("/game/invest")
                        .contentType("application/json")
                        .content("""
                                {"investmentType":"BUY_SEEDLING"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.money").value(7))
                .andExpect(jsonPath("$.plantation.trees.length()").value(16));

        GameState updatedState = gameStateService.getState();
        assertFieldTreeCount(updatedState, 1, 8);
        assertFieldTreeCount(updatedState, 2, 8);
    }

    @Test
    void nextPhaseMarksWinAtRoundFifteen() throws Exception {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.MOVE_MARKER);
        state.setCurrentRound(15);
        gameStateService.createNewGame(state);

        mockMvc.perform(post("/game/next-phase"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameOver").value(true))
                .andExpect(jsonPath("$.gameResult").value("WIN"))
                .andExpect(jsonPath("$.currentPhase").value("MOVE_MARKER"));
    }

    @Test
    void eventOptionsAndSelectionFlowThroughApi() throws Exception {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.DRAW_EVENT);
        state.setMoney(10);
        state.setEventDrawPile(new ArrayList<>(List.of("EVT_FUNDING", "EVT_INTEREST_RATES")));
        gameStateService.createNewGame(state);

        mockMvc.perform(get("/game/event/options"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].optionIndex").value(0))
                .andExpect(jsonPath("$[1].optionIndex").value(1));

        mockMvc.perform(post("/game/event/select")
                        .contentType("application/json")
                        .content("""
                                {"optionIndex":0}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPhase").value("REFILL_CARDS"))
                .andExpect(jsonPath("$.money").value(15))
                .andExpect(jsonPath("$.lastEventResult.cardId").value("EVT_FUNDING"))
                .andExpect(jsonPath("$.pendingEventOptions.length()").value(0));
    }

    @Test
    void buyProductionEndpointBuysVisibleCardAndUpdatesState() throws Exception {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.INVEST);
        state.setMoney(20);
        state.setMarketCardIds(new ArrayList<>(Arrays.asList("ST_FAST_GROWTH", null, null, null, null)));
        gameStateService.createNewGame(state);

        mockMvc.perform(post("/game/invest/production")
                        .contentType("application/json")
                        .content("""
                                {"cardId":"ST_FAST_GROWTH"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.money").value(15))
                .andExpect(jsonPath("$.marketCardIds[0]").doesNotExist())
                .andExpect(jsonPath("$.productionDiscardPile[0]").value("ST_FAST_GROWTH"))
                .andExpect(jsonPath("$.shortTermUsedThisRound[0]").value("ST_FAST_GROWTH"))
                .andExpect(jsonPath("$.scoreTrack.economy").value(2))
                .andExpect(jsonPath("$.scoreTrack.environment").value(-1))
                .andExpect(jsonPath("$.scoreTrack.health").value(-1));
    }

    private void addTrees(GameState state, int fieldPosition, int count) {
        for (int i = 0; i < count; i++) {
            Tree tree = new Tree();
            tree.setType(TreeType.SEEDLING);
            tree.setFieldPosition(fieldPosition);
            state.getPlantation().getTrees().add(tree);
        }
    }

    private void assertFieldTreeCount(GameState state, int fieldPosition, long expectedCount) {
        long actualCount = state.getPlantation().getTrees().stream()
                .filter(tree -> tree.getFieldPosition() == fieldPosition)
                .count();

        if (actualCount != expectedCount) {
            throw new AssertionError("Expected " + expectedCount + " trees in field " + fieldPosition + " but found " + actualCount);
        }
    }
}
