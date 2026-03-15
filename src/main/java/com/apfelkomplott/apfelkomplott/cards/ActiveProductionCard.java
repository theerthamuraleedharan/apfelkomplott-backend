package com.apfelkomplott.apfelkomplott.cards;

import com.apfelkomplott.apfelkomplott.Enum.PlantationSize;

public class ActiveProductionCard {

    private String cardId;
    private int purchasedRound;

    private PlantationSize plantationSizeAtPurchase;

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
}
