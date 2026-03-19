package com.apfelkomplott.apfelkomplott.entity;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.cards.ActiveProductionCard;
import com.apfelkomplott.apfelkomplott.cards.ProductionCardDef;
import com.apfelkomplott.apfelkomplott.controller.dto.SellResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {

    private int currentRound;
    private GamePhase currentPhase;
    private int money;
    private SellResult lastSellResult;
    private ScoreTrack scoreTrack;
    private Plantation plantation;
    private ScoreResult lastScoreResult;
    private ScoreResult productionCardFinalScoreResult;
    private FarmingMode farmingMode;
    private List<ProductionCard> activeProductionCards;
    private List<String> marketCardIds = new ArrayList<>();
    private List<ActiveProductionCard> activeLongTerm = new ArrayList<>();
    private List<String> shortTermUsedThisRound = new ArrayList<>();
    private List<String> productionDrawPile = new ArrayList<>();
    private List<String> productionDiscardPile = new ArrayList<>();
    private List<String> eventDrawPile = new ArrayList<>();
    private List<String> eventDiscardPile = new ArrayList<>();
    private List<String> pendingEventOptions = new ArrayList<>();
    private List<ProductionCardDef> market = new ArrayList<>();
    private EventResolution lastEventResult;
    private RoundEventImpact roundEventImpact = new RoundEventImpact();
    private Map<String, Integer> productionCardCostModifiers = new HashMap<>();

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
        this.money = 60;

        this.scoreTrack = new ScoreTrack();
        this.plantation = new Plantation();

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

    public FarmingMode getFarmingMode() {
        return farmingMode;
    }

    public void setFarmingMode(FarmingMode farmingMode) {
        this.farmingMode = farmingMode;
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

    public ScoreResult getProductionCardFinalScoreResult() {
        return productionCardFinalScoreResult;
    }

    public void setProductionCardFinalScoreResult(ScoreResult productionCardFinalScoreResult) {
        this.productionCardFinalScoreResult = productionCardFinalScoreResult;   
    }

    public List<String> getEventDrawPile() {
        return eventDrawPile;
    }

    public void setEventDrawPile(List<String> eventDrawPile) {
        this.eventDrawPile = eventDrawPile;
    }

    public List<String> getEventDiscardPile() {
        return eventDiscardPile;
    }

    public void setEventDiscardPile(List<String> eventDiscardPile) {
        this.eventDiscardPile = eventDiscardPile;
    }

    public List<String> getPendingEventOptions() {
        return pendingEventOptions;
    }

    public void setPendingEventOptions(List<String> pendingEventOptions) {
        this.pendingEventOptions = pendingEventOptions;
    }

    public EventResolution getLastEventResult() {
        return lastEventResult;
    }

    public void setLastEventResult(EventResolution lastEventResult) {
        this.lastEventResult = lastEventResult;
    }

    public RoundEventImpact getRoundEventImpact() {
        return roundEventImpact;
    }

    public void setRoundEventImpact(RoundEventImpact roundEventImpact) {
        this.roundEventImpact = roundEventImpact;
    }

    public int getCurrentSaleBonusPerApple() {
        return plantation == null ? 0 : plantation.getApplePriceModifier();
    }

    public Map<String, Integer> getProductionCardCostModifiers() {
        return productionCardCostModifiers;
    }

    public void setProductionCardCostModifiers(Map<String, Integer> productionCardCostModifiers) {
        this.productionCardCostModifiers = productionCardCostModifiers;
    }
}

