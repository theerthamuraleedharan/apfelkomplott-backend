package com.apfelkomplott.apfelkomplott.entity;

public class ProductionCardDefinition {

    private final String name;
    private final int cost;
    private final CardCategory category;

    private final int economyEffect;
    private final int environmentEffect;
    private final int healthEffect;

    private final int startRound;
    private final int endRound;

    private final String description;

    public ProductionCardDefinition(
            String name,
            int cost,
            CardCategory category,
            int economyEffect,
            int environmentEffect,
            int healthEffect,
            int startRound,
            int endRound,
            String description
    ) {
        this.name = name;
        this.cost = cost;
        this.category = category;
        this.economyEffect = economyEffect;
        this.environmentEffect = environmentEffect;
        this.healthEffect = healthEffect;
        this.startRound = startRound;
        this.endRound = endRound;
        this.description = description;
    }

    // ===== Getters =====

    public String getName() { return name; }
    public int getCost() { return cost; }
    public CardCategory getCategory() { return category; }

    public int getEconomyEffect() { return economyEffect; }
    public int getEnvironmentEffect() { return environmentEffect; }
    public int getHealthEffect() { return healthEffect; }

    public int getStartRound() { return startRound; }
    public int getEndRound() { return endRound; }

    public String getDescription() { return description; }

    // ===== Helper =====

    public boolean isActiveInRound(int round) {
        return round >= startRound && round <= endRound;
    }
}
