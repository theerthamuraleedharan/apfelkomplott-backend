package com.apfelkomplott.apfelkomplott.controller.dto;

import com.apfelkomplott.apfelkomplott.entity.GameState;

public class InvestmentActionRequest {

    private GameState gameState;
    private InvestmentType investmentType;

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public InvestmentType getInvestmentType() {
        return investmentType;
    }

    public void setInvestmentType(InvestmentType investmentType) {
        this.investmentType = investmentType;
    }
}
