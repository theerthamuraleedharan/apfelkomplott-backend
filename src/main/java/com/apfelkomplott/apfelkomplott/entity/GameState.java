package com.apfelkomplott.apfelkomplott.entity;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.cards.ActiveProductionCard;
import com.apfelkomplott.apfelkomplott.cards.ProductionCardDef;
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
    private ScoreResult lastCardScoreResult;
    private FarmingMode farmingMode;
    private EventCard lastDrawnEvent;
    private List<ProductionCard> activeProductionCards;
    private List<EventCard> eventDeck = new ArrayList<>();
    private List<EventCard> activeEvents = new ArrayList<>();
    private List<String> marketCardIds = new ArrayList<>();
    private List<ActiveProductionCard> activeLongTerm = new ArrayList<>();
    private List<String> shortTermUsedThisRound = new ArrayList<>();
    private List<String> productionDrawPile = new ArrayList<>();
    private List<String> productionDiscardPile = new ArrayList<>();
    private List<ProductionCardDef> market = new ArrayList<>();

    public List<ProductionCardDef> getMarket() {
        return market;
    }

    public void setMarket(List<ProductionCardDef> market) {
        this.market = market;
    }


    private boolean gameOver;

    public GameState() {
        this.currentRound = 1;
        this.currentPhase = GamePhase.MOVE_MARKER;
        this.money = 50;

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

    public ScoreResult getLastCardScoreResult() {
        return lastCardScoreResult;
    }

    public void setLastCardScoreResult(ScoreResult lastCardScoreResult) {
        this.lastCardScoreResult = lastCardScoreResult;
    }

    public FarmingMode getFarmingMode() {
        return farmingMode;
    }

    public void setFarmingMode(FarmingMode farmingMode) {
        this.farmingMode = farmingMode;
    }

    public List<EventCard> getEventDeck() {
        return eventDeck;
    }

    public void setEventDeck(List<EventCard> eventDeck) {
        this.eventDeck = eventDeck;
    }

    public List<EventCard> getActiveEvents() {
        return activeEvents;
    }

    public void setActiveEvents(List<EventCard> activeEvents) {
        this.activeEvents = activeEvents;
    }

    public EventCard getLastDrawnEvent() {
        return lastDrawnEvent;
    }

    public void setLastDrawnEvent(EventCard lastDrawnEvent) {
        this.lastDrawnEvent = lastDrawnEvent;
    }

    public void setScoreTrack(ScoreTrack scoreTrack) {
        this.scoreTrack = scoreTrack;
    }

    public void setActiveProductionCards(List<ProductionCard> activeProductionCards) {
        this.activeProductionCards = activeProductionCards;
    }

    public List<String> getMarketCardIds() {
        return marketCardIds;
    }

    public void setMarketCardIds(List<String> marketCardIds) {
        this.marketCardIds = marketCardIds;
    }

    public List<ActiveProductionCard> getActiveLongTerm() {
        return activeLongTerm;
    }

    public void setActiveLongTerm(List<ActiveProductionCard> activeLongTerm) {
        this.activeLongTerm = activeLongTerm;
    }

    public List<String> getShortTermUsedThisRound() {
        return shortTermUsedThisRound;
    }

    public void setShortTermUsedThisRound(List<String> shortTermUsedThisRound) {
        this.shortTermUsedThisRound = shortTermUsedThisRound;
    }

    public List<String> getProductionDrawPile() {
        return productionDrawPile;
    }

    public void setProductionDrawPile(List<String> productionDrawPile) {
        this.productionDrawPile = productionDrawPile;
    }

    public List<String> getProductionDiscardPile() {
        return productionDiscardPile;
    }

    public void setProductionDiscardPile(List<String> productionDiscardPile) {
        this.productionDiscardPile = productionDiscardPile;
    }
}

