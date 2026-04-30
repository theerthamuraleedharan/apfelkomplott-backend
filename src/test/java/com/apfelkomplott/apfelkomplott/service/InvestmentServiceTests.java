package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.controller.dto.InvestmentType;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameResult;
import com.apfelkomplott.apfelkomplott.entity.GameState;
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

    private GameState newInvestState() {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.INVEST);
        return state;
    }
}
