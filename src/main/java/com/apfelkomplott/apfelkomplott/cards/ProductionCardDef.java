package com.apfelkomplott.apfelkomplott.cards;

import com.apfelkomplott.apfelkomplott.Enum.CardDeck;
import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ProductionCardDef {

    private String id;
    private String name;
    private CardDeck deck;
    private String category;

    private CostDef cost;

    private List<EffectDef> effects; // normal effects
    private Map<FarmingMode, List<EffectDef>> effectsByMode; // optional

    private List<String> requires;
    private String image;

    private List<MediaItem> media;

    public List<MediaItem> getMedia() { return media; }
    public void setMedia(List<MediaItem> media) { this.media = media; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CardDeck getDeck() {
        return deck;
    }

    public void setDeck(CardDeck deck) {
        this.deck = deck;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public CostDef getCost() {
        return cost;
    }

    public void setCost(CostDef cost) {
        this.cost = cost;
    }

    public List<EffectDef> getEffects() {
        return effects;
    }

    public void setEffects(List<EffectDef> effects) {
        this.effects = effects;
    }

    public Map<FarmingMode, List<EffectDef>> getEffectsByMode() {
        return effectsByMode;
    }

    public void setEffectsByMode(Map<FarmingMode, List<EffectDef>> effectsByMode) {
        this.effectsByMode = effectsByMode;
    }

    public List<String> getRequires() {
        return requires;
    }

    public void setRequires(List<String> requires) {
        this.requires = requires;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
