package com.apfelkomplott.apfelkomplott.controller.dto;

import com.apfelkomplott.apfelkomplott.cards.ProductionCardDef;

public class ActiveLongTermCardView {
    private String cardId;
    private String name;
    private int currentYear;
    private int roundsRemaining;
    private ProductionCardDef definition;

    public ActiveLongTermCardView() {
    }

    public ActiveLongTermCardView(String cardId, String name, int currentYear, int roundsRemaining, ProductionCardDef definition) {
        this.cardId = cardId;
        this.name = name;
        this.currentYear = currentYear;
        this.roundsRemaining = roundsRemaining;
        this.definition = definition;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public int getRoundsRemaining() {
        return roundsRemaining;
    }

    public void setRoundsRemaining(int roundsRemaining) {
        this.roundsRemaining = roundsRemaining;
    }

    public ProductionCardDef getDefinition() {
        return definition;
    }

    public void setDefinition(ProductionCardDef definition) {
        this.definition = definition;
    }

    

}
