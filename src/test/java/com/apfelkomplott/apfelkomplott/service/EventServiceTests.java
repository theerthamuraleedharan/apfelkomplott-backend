package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.cards.ProductionCardIds;
import com.apfelkomplott.apfelkomplott.controller.dto.HiddenEventCardDto;
import com.apfelkomplott.apfelkomplott.entity.EventResolution;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.Tree;
import com.apfelkomplott.apfelkomplott.entity.TreeType;
import com.apfelkomplott.apfelkomplott.repository.EventCardRepository;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventServiceTests {

    private static final String FUNDING_EVENT_ID = "EVT_FUNDING";
    private static final String SEVERE_WEATHER_EVENT_ID = "EVT_SEVERE_WEATHER";
    private static final String DROUGHT_EVENT_ID = "EVT_DROUGHT";
    private static final String INTEREST_RATES_EVENT_ID = "EVT_INTEREST_RATES";

    private final EventService service = createService();

    @Test
    void getHiddenOptionsRequiresDrawEventPhase() {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.SELL);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.getHiddenOptions(state));

        assertEquals("Event cards can only be handled during DRAW_EVENT phase.", error.getMessage());
    }

    @Test
    void getHiddenOptionsDrawsAtMostTwoCards() {
        GameState state = newDrawEventState();
        state.getEventDrawPile().addAll(List.of(FUNDING_EVENT_ID, INTEREST_RATES_EVENT_ID, SEVERE_WEATHER_EVENT_ID));

        List<HiddenEventCardDto> options = service.getHiddenOptions(state);

        assertEquals(2, options.size());
        assertEquals(0, options.get(0).getOptionIndex());
        assertEquals(1, options.get(1).getOptionIndex());
        assertEquals(List.of(FUNDING_EVENT_ID, INTEREST_RATES_EVENT_ID), state.getPendingEventOptions());
        assertEquals(List.of(SEVERE_WEATHER_EVENT_ID), state.getEventDrawPile());
    }

    @Test
    void selectEventAppliesMoneyDeltaAndAdvancesPhase() {
        GameState state = newDrawEventState();
        state.setMoney(10);
        state.getPendingEventOptions().addAll(List.of(FUNDING_EVENT_ID, INTEREST_RATES_EVENT_ID));

        EventResolution resolution = service.selectEvent(state, 0);

        assertEquals(15, state.getMoney());
        assertEquals(GamePhase.REFILL_CARDS, state.getCurrentPhase());
        assertTrue(state.getEventDiscardPile().contains(FUNDING_EVENT_ID));
        assertTrue(state.getEventDrawPile().contains(INTEREST_RATES_EVENT_ID));
        assertTrue(state.getPendingEventOptions().isEmpty());
        assertEquals(FUNDING_EVENT_ID, state.getLastEventResult().getCardId());
        assertEquals(5, resolution.getMoneyChange());
        assertTrue(resolution.getEffects().contains("+5 money"));
    }

    @Test
    void selectEventRejectsInvalidOptionIndex() {
        GameState state = newDrawEventState();
        state.getPendingEventOptions().add(FUNDING_EVENT_ID);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> service.selectEvent(state, 5));

        assertEquals("Invalid event option index: 5", error.getMessage());
    }

    @Test
    void selectEventAppliesHarvestLossForCurrentPlantationSize() {
        GameState state = newDrawEventState();
        addMatureTrees(state, 5);
        state.getPendingEventOptions().add(SEVERE_WEATHER_EVENT_ID);

        EventResolution resolution = service.selectEvent(state, 0);

        assertEquals(4, resolution.getExpectedHarvestLoss());
        assertEquals(4, service.calculateHarvestLoss(state));
        assertFalse(state.getRoundEventImpact().getHarvestLossByPlantationSize().isEmpty());
        assertEquals(3, state.getRoundEventImpact().getHarvestLossDivisor());
    }

    @Test
    void selectEventAppliesProductionCardCostModifiersForTargetGroups() {
        GameState state = newDrawEventState();
        state.setFarmingMode(FarmingMode.CONVENTIONAL);
        state.getPendingEventOptions().add(DROUGHT_EVENT_ID);

        EventResolution resolution = service.selectEvent(state, 0);

        assertEquals(1, state.getProductionCardCostModifiers().get(ProductionCardIds.WATER_MANAGEMENT));
        assertEquals(1, state.getProductionCardCostModifiers().get(ProductionCardIds.WATER_MANAGEMENT_PRIVATE_WELL));
        assertEquals(1, state.getProductionCardCostModifiers().get(ProductionCardIds.WATER_MANAGEMENT_ECO));
        assertEquals(1, state.getProductionCardCostModifiers().get(ProductionCardIds.SHADE_NETS));
        assertEquals(2, resolution.getEffects().size());
        assertTrue(resolution.getEffects().stream().anyMatch(effect -> effect.contains("Water Management")));
        assertTrue(resolution.getEffects().stream().anyMatch(effect -> effect.contains("Shade Nets")));
    }

    private EventService createService() {
        try {
            return new EventService(new EventCardRepository(new ObjectMapper()));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create EventService for tests", e);
        }
    }

    private GameState newDrawEventState() {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.DRAW_EVENT);
        return state;
    }

    private void addMatureTrees(GameState state, int count) {
        for (int i = 0; i < count; i++) {
            Tree tree = new Tree();
            tree.setFieldPosition(3);
            tree.setType(TreeType.SEEDLING);
            state.getPlantation().getTrees().add(tree);
        }
    }
}
