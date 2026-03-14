package com.apfelkomplott.apfelkomplott.cards;

import java.util.ArrayList;
import java.util.List;

public class ActiveProductionCard {
    private String cardId;
    private int purchasedRound;
    private List<Integer> plantationLayout = new ArrayList<>();

    public ActiveProductionCard(String cardId, int purchasedRound) {
        this.cardId = cardId;
        this.purchasedRound = purchasedRound;
        this.plantationLayout = new ArrayList<>();
    }

    public ActiveProductionCard(String cardId, int purchasedRound, List<Integer> plantationLayout) {
        this.cardId = cardId;
        this.purchasedRound = purchasedRound;
        this.plantationLayout = plantationLayout == null ? new ArrayList<>() : new ArrayList<>(plantationLayout);
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

    public List<Integer> getPlantationLayout() {
        return plantationLayout;
    }

    public void setPlantationLayout(List<Integer> plantationLayout) {
        this.plantationLayout = plantationLayout == null ? new ArrayList<>() : new ArrayList<>(plantationLayout);
    }
}
