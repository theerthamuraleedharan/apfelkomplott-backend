package com.apfelkomplott.apfelkomplott.entity;

import com.apfelkomplott.apfelkomplott.Enum.PlantationSize;
import com.apfelkomplott.apfelkomplott.cards.MediaItem;

import java.util.ArrayList;
import java.util.List;

public class EventResolution {

    private String cardId;
    private String cardName;
    private String description;
    private int moneyChange;
    private PlantationSize plantationSize;
    private int expectedHarvestLoss;
    private int saleBonusPerAppleChange;
    private int resultingSaleBonusPerApple;
    private int productionCardCostChange;
    private List<MediaItem> media = new ArrayList<>();
    private List<String> effects = new ArrayList<>();

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMoneyChange() {
        return moneyChange;
    }

    public void setMoneyChange(int moneyChange) {
        this.moneyChange = moneyChange;
    }

    public PlantationSize getPlantationSize() {
        return plantationSize;
    }

    public void setPlantationSize(PlantationSize plantationSize) {
        this.plantationSize = plantationSize;
    }

    public int getExpectedHarvestLoss() {
        return expectedHarvestLoss;
    }

    public void setExpectedHarvestLoss(int expectedHarvestLoss) {
        this.expectedHarvestLoss = expectedHarvestLoss;
    }

    public int getSaleBonusPerAppleChange() {
        return saleBonusPerAppleChange;
    }

    public void setSaleBonusPerAppleChange(int saleBonusPerAppleChange) {
        this.saleBonusPerAppleChange = saleBonusPerAppleChange;
    }

    public int getResultingSaleBonusPerApple() {
        return resultingSaleBonusPerApple;
    }

    public void setResultingSaleBonusPerApple(int resultingSaleBonusPerApple) {
        this.resultingSaleBonusPerApple = resultingSaleBonusPerApple;
    }

    public int getProductionCardCostChange() {
        return productionCardCostChange;
    }

    public void setProductionCardCostChange(int productionCardCostChange) {
        this.productionCardCostChange = productionCardCostChange;
    }

    public List<MediaItem> getMedia() {
        return media;
    }

    public void setMedia(List<MediaItem> media) {
        this.media = media;
    }

    public List<String> getEffects() {
        return effects;
    }

    public void setEffects(List<String> effects) {
        this.effects = effects;
    }

    public void addEffect(String effect) {
        effects.add(effect);
    }
}
