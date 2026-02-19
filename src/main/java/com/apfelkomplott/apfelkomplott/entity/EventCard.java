package com.apfelkomplott.apfelkomplott.entity;

import com.apfelkomplott.apfelkomplott.Enum.EventEffectType;
import com.apfelkomplott.apfelkomplott.Enum.EventTiming;

public class EventCard {

    private String title;
    private String description;
    private EventTiming timing;
    private EventEffectType effectType;

    public EventCard(String title, String description,
                     EventTiming timing,
                     EventEffectType effectType) {
        this.title = title;
        this.description = description;
        this.timing = timing;
        this.effectType = effectType;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public EventTiming getTiming() { return timing; }
    public EventEffectType getEffectType() { return effectType; }
}
