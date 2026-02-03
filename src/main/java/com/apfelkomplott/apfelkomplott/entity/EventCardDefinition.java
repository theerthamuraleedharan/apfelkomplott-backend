package com.apfelkomplott.apfelkomplott.entity;

public class EventCardDefinition {

    private final String name;
    private final String description;
    private final EventType type;

    public EventCardDefinition(String name, String description, EventType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public EventType getType() { return type; }
}
