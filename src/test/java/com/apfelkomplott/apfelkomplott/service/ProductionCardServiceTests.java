package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.Enum.CardDeck;
import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.Enum.PlantationSize;
import com.apfelkomplott.apfelkomplott.cards.ActiveProductionCard;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.ScoreResult;
import com.apfelkomplott.apfelkomplott.entity.Tree;
import com.apfelkomplott.apfelkomplott.entity.TreeType;
import com.apfelkomplott.apfelkomplott.repository.ProductionCardRepository;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductionCardServiceTests {

    private static final String SHORT_TERM_CARD_ID = "ST_FAST_GROWTH";
    private static final String LONG_TERM_CARD_ID = "LT_PRODUCTION_METHOD";
    private static final String PLANTATION_SIZE_CARD_ID = "LT_GROWING_AREAS-19";

    private final ProductionCardService service = createService();

    @Test
    void buyCardRejectsCardThatIsNotInMarket() {
        GameState state = newState(FarmingMode.CONVENTIONAL, 20);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.buyCard(state, SHORT_TERM_CARD_ID));

        assertEquals("Card is not in market row.", error.getMessage());
    }

    @Test
    void buyShortTermCardDeductsMoneyAndAppliesImmediateEffects() {
        GameState state = newState(FarmingMode.CONVENTIONAL, 20);
        state.setMarketCardIds(new ArrayList<>(Arrays.asList(SHORT_TERM_CARD_ID, null, null, null, null)));

        ScoreResult result = service.buyCard(state, SHORT_TERM_CARD_ID);

        assertEquals(15, state.getMoney());
        assertNull(state.getMarketCardIds().get(0));
        assertTrue(state.getProductionDiscardPile().contains(SHORT_TERM_CARD_ID));
        assertTrue(state.getShortTermUsedThisRound().contains(SHORT_TERM_CARD_ID));
        assertTrue(state.getActiveLongTerm().isEmpty());
        assertEquals(2, state.getScoreTrack().getEconomy());
        assertEquals(-1, state.getScoreTrack().getEnvironment());
        assertEquals(-1, state.getScoreTrack().getHealth());
        assertNotNull(result);
        assertEquals(0, result.getEconomyChange());
    }

    @Test
    void buyLongTermProductionMethodSwitchesFarmModeAndStoresCard() {
        GameState state = newState(FarmingMode.CONVENTIONAL, 25);
        state.setMarketCardIds(new ArrayList<>(Arrays.asList(LONG_TERM_CARD_ID, null, null, null, null)));

        ScoreResult result = service.buyCard(state, LONG_TERM_CARD_ID);

        assertEquals(15, state.getMoney());
        assertEquals(FarmingMode.ORGANIC, state.getFarmingMode());
        assertEquals(1, state.getActiveLongTerm().size());
        assertEquals(LONG_TERM_CARD_ID, state.getActiveLongTerm().get(0).getCardId());
        assertTrue(state.getProductionDiscardPile().isEmpty());
        assertTrue(state.getShortTermUsedThisRound().isEmpty());
        assertEquals(0, state.getScoreTrack().getEconomy());
        assertNotNull(result);
    }

    @Test
    void buyCardAppliesProductionCostModifiers() {
        GameState state = newState(FarmingMode.CONVENTIONAL, 8);
        state.setMarketCardIds(new ArrayList<>(Arrays.asList(SHORT_TERM_CARD_ID, null, null, null, null)));
        state.getProductionCardCostModifiers().put(SHORT_TERM_CARD_ID, 2);

        service.buyCard(state, SHORT_TERM_CARD_ID);

        assertEquals(1, state.getMoney());
    }

    @Test
    void buyCardFailsWhenMoneyIsTooLow() {
        GameState state = newState(FarmingMode.CONVENTIONAL, 9);
        state.setMarketCardIds(new ArrayList<>(Arrays.asList(LONG_TERM_CARD_ID, null, null, null, null)));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.buyCard(state, LONG_TERM_CARD_ID));

        assertEquals("Not enough money.", error.getMessage());
    }

    @Test
    void applyLongTermCardScoringUsesPlantationSizeAtPurchase() {
        GameState state = newState(FarmingMode.CONVENTIONAL, 20);
        state.setCurrentRound(2);
        addTrees(state, 10);
        state.getActiveLongTerm().add(new ActiveProductionCard(PLANTATION_SIZE_CARD_ID, 1, PlantationSize.SMALL));

        ScoreResult result = service.applyLongTermCardScoring(state);

        assertEquals(-1, result.getEconomyChange());
        assertEquals(1, result.getEnvironmentChange());
        assertEquals(0, result.getHealthChange());
        assertFalse(result.getReasons().isEmpty());
        assertEquals(-1, state.getScoreTrack().getEconomy());
        assertEquals(1, state.getScoreTrack().getEnvironment());
        assertEquals(0, state.getScoreTrack().getHealth());
    }

    @Test
    void refillMarketToFiveBuildsThreeShortTermAndTwoLongTermCards() {
        GameState state = newState(FarmingMode.CONVENTIONAL, 20);

        service.initDeckAndMarket(state);

        assertEquals(5, state.getMarketCardIds().size());
        assertFalse(state.getMarketCardIds().contains(null));

        long shortTermCount = state.getMarketCardIds().stream()
                .map(id -> createServiceRepository().getById(id))
                .filter(card -> card.getDeck() == CardDeck.SHORT_TERM)
                .count();
        long longTermCount = state.getMarketCardIds().stream()
                .map(id -> createServiceRepository().getById(id))
                .filter(card -> card.getDeck() == CardDeck.LONG_TERM)
                .count();

        assertEquals(3, shortTermCount);
        assertEquals(2, longTermCount);
    }

    private ProductionCardService createService() {
        try {
            return new ProductionCardService(new ProductionCardRepository(new ObjectMapper()));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create ProductionCardService for tests", e);
        }
    }

    private ProductionCardRepository createServiceRepository() {
        try {
            return new ProductionCardRepository(new ObjectMapper());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create ProductionCardRepository for tests", e);
        }
    }

    private GameState newState(FarmingMode farmingMode, int money) {
        GameState state = new GameState();
        state.setFarmingMode(farmingMode);
        state.setMoney(money);
        return state;
    }

    private void addTrees(GameState state, int count) {
        for (int i = 0; i < count; i++) {
            Tree tree = new Tree();
            tree.setFieldPosition(3);
            tree.setType(TreeType.SEEDLING);
            state.getPlantation().getTrees().add(tree);
        }
    }
}
