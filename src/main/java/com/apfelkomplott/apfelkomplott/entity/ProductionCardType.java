package com.apfelkomplott.apfelkomplott.entity;

public enum ProductionCardType {

    SELF_HARVESTER(
            5,
            CardCategory.PRODUCTION,
            1, 0, 0,
            1, 2
    ),

    OPTIMIZE_HARVEST(
            15,
            CardCategory.MIXED,
            1, 1, 1,
            1, 1
    ),

    WATER_MANAGEMENT(
            10,
            CardCategory.ENVIRONMENT,
            0, 1, 0,
            1, 3
    ),

    SHADE_NETS(
            8,
            CardCategory.HEALTH,
            0, 0, 1,
            1, 3
    ),

    PEST_CONTROL(
            12,
            CardCategory.MIXED,
            0, -1, 1,
            1, 2
    );

    // ===== Fields =====

    private final int cost;
    private final CardCategory category;

    private final int economyEffect;
    private final int environmentEffect;
    private final int healthEffect;

    // IMPORTANT: rounds, not years
    private final int startRound;
    private final int endRound;

    // ===== Constructor =====

    ProductionCardType(int cost,
                       CardCategory category,
                       int economyEffect,
                       int environmentEffect,
                       int healthEffect,
                       int startRound,
                       int endRound) {

        this.cost = cost;
        this.category = category;
        this.economyEffect = economyEffect;
        this.environmentEffect = environmentEffect;
        this.healthEffect = healthEffect;
        this.startRound = startRound;
        this.endRound = endRound;
    }

    // ===== Logic =====

    public boolean isActiveInRound(int round) {
        return round >= startRound && round <= endRound;
    }

    // ===== Getters =====

    public int getCost() {
        return cost;
    }

    public CardCategory getCategory() {
        return category;
    }

    public int getEconomyEffect() {
        return economyEffect;
    }

    public int getEnvironmentEffect() {
        return environmentEffect;
    }

    public int getHealthEffect() {
        return healthEffect;
    }

    public int getStartRound() {
        return startRound;
    }

    public int getEndRound() {
        return endRound;
    }
}
