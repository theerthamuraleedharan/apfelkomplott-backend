package com.apfelkomplott.apfelkomplott.cards;

import com.apfelkomplott.apfelkomplott.Enum.PlantationSize;

import java.util.List;

public class ActiveProductionCard {

    private String cardId;
    private int purchasedRound;

    private PlantationSize plantationSizeAtPurchase;
    private List<Integer> remainingYears;
    private Integer currentYear;
    private Integer finalApplicableYear;

    public ActiveProductionCard() {
    }

    public ActiveProductionCard(String cardId, int purchasedRound) {
        this.cardId = cardId;
        this.purchasedRound = purchasedRound;
    }


    public ActiveProductionCard(String cardId, int purchasedRound, PlantationSize plantationSizeAtPurchase) {
        this.cardId = cardId;
        this.purchasedRound = purchasedRound;
        this.plantationSizeAtPurchase = plantationSizeAtPurchase;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public int getPurchasedRound() {
        return purchasedRound;
    }

    public void setPurchasedRound(int purchasedRound) {
        this.purchasedRound = purchasedRound;
    }

    public PlantationSize getPlantationSizeAtPurchase() {
        return plantationSizeAtPurchase;
    }

    public void setPlantationSizeAtPurchase(PlantationSize plantationSizeAtPurchase) {
        this.plantationSizeAtPurchase = plantationSizeAtPurchase;
    }

    public List<Integer> getRemainingYears() {
        return remainingYears;
    }

    public void setRemainingYears(List<Integer> remainingYears) {
        this.remainingYears = remainingYears;
    }

    public Integer getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(Integer currentYear) {
        this.currentYear = currentYear;
    }

    public Integer getFinalApplicableYear() {
        return finalApplicableYear;
    }

    public void setFinalApplicableYear(Integer finalApplicableYear) {
        this.finalApplicableYear = finalApplicableYear;
    }
}
