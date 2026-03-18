package com.apfelkomplott.apfelkomplott.cards;

import java.util.ArrayList;
import java.util.List;

public class EventCardDef {

    private String id;
    private String name;
    private String description;
    private List<EventEffectDef> effects = new ArrayList<>();
    private List<MediaItem> media = new ArrayList<>();

    public EventCardDef() {
    }

    public EventCardDef(String id, String name, String description, List<EventEffectDef> effects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.effects = effects;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EventEffectDef> getEffects() {
        return effects;
    }

    public void setEffects(List<EventEffectDef> effects) {
        this.effects = effects;
    }

    public List<MediaItem> getMedia() {
        return media;
    }

    public void setMedia(List<MediaItem> media) {
        this.media = media;
    }
}
