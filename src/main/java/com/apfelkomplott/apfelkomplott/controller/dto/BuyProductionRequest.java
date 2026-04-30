package com.apfelkomplott.apfelkomplott.controller.dto;

import jakarta.validation.constraints.NotBlank;

public class BuyProductionRequest {

    @NotBlank(message = "cardId is required.")
    private String cardId;

    public BuyProductionRequest() {
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
