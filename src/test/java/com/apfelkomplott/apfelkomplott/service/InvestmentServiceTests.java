package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.controller.dto.InvestmentType;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameResult;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.Tree;
import com.apfelkomplott.apfelkomplott.entity.TreeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InvestmentServiceTests {

    private final InvestmentService service = new InvestmentService();

    @Test
    void investRejectsBuyingSeedlingWithoutEnoughMoney() {
        GameState state = newInvestState();
        state.setMoney(2);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.invest(state, InvestmentType.BUY_SEEDLING));

        assertEquals("Not enough money to buy a seedling.", error.getMessage());
        assertSame(GameResult.IN_PROGRESS, state.getGameResult());
    }

    @Test
    void investRejectsBuyingCrateWithoutEnoughMoney() {
        GameState state = newInvestState();
        state.setMoney(2);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.invest(state, InvestmentType.BUY_CRATE));

        assertEquals("Not enough money to buy a crate.", error.getMessage());
        assertSame(GameResult.IN_PROGRESS, state.getGameResult());
    }

    @Test
    void investAllowsBuyingSeedlingWhenPlantationIsLargeButFieldOneHasSpace() {
        GameState state = newInvestState();
        state.setMoney(10);
        addTrees(state, 1, 7);
        addTrees(state, 2, 8);

        service.invest(state, InvestmentType.BUY_SEEDLING);

        assertEquals(16, state.getPlantation().getTrees().size());
        assertEquals(8, countTreesInField(state, 1));
        assertEquals(7, state.getMoney());
    }

    @Test
    void investRejectsBuyingSeedlingWhenFieldOneIsFull() {
        GameState state = newInvestState();
        state.setMoney(10);
        addTrees(state, 1, 8);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.invest(state, InvestmentType.BUY_SEEDLING));

        assertEquals("A field can contain at most 8 plants.", error.getMessage());
    }

    @Test
    void investAllowsBuyingPreGrownTreeWhenPlantationIsLargeButFieldTwoHasSpace() {
        GameState state = newInvestState();
        state.setMoney(10);
        addTrees(state, 1, 8);
        addTrees(state, 2, 7);

        service.invest(state, InvestmentType.BUY_PRE_GROWN_TREE);

        assertEquals(16, state.getPlantation().getTrees().size());
        assertEquals(8, countTreesInField(state, 2));
        assertEquals(6, state.getMoney());
    }

    @Test
    void investRejectsBuyingPreGrownTreeWhenFieldTwoIsFull() {
        GameState state = newInvestState();
        state.setMoney(10);
        addTrees(state, 2, 8);

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.invest(state, InvestmentType.BUY_PRE_GROWN_TREE));

        assertEquals("A field can contain at most 8 plants.", error.getMessage());
    }

    private GameState newInvestState() {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.INVEST);
        return state;
    }

    private void addTrees(GameState state, int fieldPosition, int count) {
        for (int i = 0; i < count; i++) {
            Tree tree = new Tree();
            tree.setType(TreeType.SEEDLING);
            tree.setFieldPosition(fieldPosition);
            state.getPlantation().getTrees().add(tree);
        }
    }

    private long countTreesInField(GameState state, int fieldPosition) {
        return state.getPlantation().getTrees().stream()
                .filter(tree -> tree.getFieldPosition() == fieldPosition)
                .count();
    }
}
