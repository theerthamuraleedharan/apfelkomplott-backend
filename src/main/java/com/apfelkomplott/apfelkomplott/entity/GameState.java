package com.apfelkomplott.apfelkomplott.entity;

import com.apfelkomplott.apfelkomplott.controller.dto.SellResult;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    private int currentRound;
    private GamePhase currentPhase;
    private int money;
    private SellResult lastSellResult;
    private ScoreTrack scoreTrack;
    private Plantation plantation;
    private ScoreResult lastScoreResult;
    private List<ProductionCard> activeProductionCards;
    private List<EventCardDefinition> activeEvents = new ArrayList<>();
    private boolean gameOver;

    public GameState() {
        this.currentRound = 1;
        this.currentPhase = GamePhase.MOVE_MARKER;
        this.money = 30;

        this.scoreTrack = new ScoreTrack();
        this.plantation = new Plantation();

        this.activeEvents = new ArrayList<>();
        this.activeProductionCards = new ArrayList<>();

        this.gameOver = false;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(GamePhase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public ScoreTrack getScoreTrack() {
        return scoreTrack;
    }

    public Plantation getPlantation() {
        return plantation;
    }

    public List<EventCardDefinition> getActiveEvents() {
        return activeEvents;
    }

    public List<ProductionCard> getActiveProductionCards() {
        return activeProductionCards;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public SellResult getLastSellResult() {
        return lastSellResult;
    }

    public void setLastSellResult(SellResult lastSellResult) {
        this.lastSellResult = lastSellResult;
    }

    public ScoreResult getLastScoreResult() {
        return lastScoreResult;
    }

    public void setLastScoreResult(ScoreResult lastScoreResult) {
        this.lastScoreResult = lastScoreResult;
    }

}

