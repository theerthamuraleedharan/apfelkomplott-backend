package com.apfelkomplott.apfelkomplott.cards;

public class ActiveProductionCard {

    private String cardId;
    private int purchasedRound;

    public ActiveProductionCard() {
    }

    public ActiveProductionCard(String cardId, int purchasedRound) {
        this.cardId = cardId;
        this.purchasedRound = purchasedRound;
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

}
