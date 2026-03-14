package com.apfelkomplott.apfelkomplott.controller.dto;

import java.util.List;

public class BuyProductionRequest {

    private String cardId;
    private List<Integer> plantationLayout;

    public BuyProductionRequest() {
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public List<Integer> getPlantationLayout() {
        return plantationLayout;
    }

    public void setPlantationLayout(List<Integer> plantationLayout) {
        this.plantationLayout = plantationLayout;
    }
}
