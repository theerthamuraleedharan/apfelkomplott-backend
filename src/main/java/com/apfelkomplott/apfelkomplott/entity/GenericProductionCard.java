package com.apfelkomplott.apfelkomplott.entity;

import com.apfelkomplott.apfelkomplott.entity.*;
import java.util.HashSet;
import java.util.Set;

public class GenericProductionCard implements ProductionCard {

    private final String name;
    private final int economyEffect;
    private final int environmentEffect;
    private final int healthEffect;
    private final int startRound;
    private final int endRound;

    private final Set<Integer> appliedRounds = new HashSet<>();

    public GenericProductionCard(
            String name,
            int economyEffect,
            int environmentEffect,
            int healthEffect,
            int startRound,
            int endRound
    ) {
        this.name = name;
        this.economyEffect = economyEffect;
        this.environmentEffect = environmentEffect;
        this.healthEffect = healthEffect;
        this.startRound = startRound;
        this.endRound = endRound;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void apply(GameState state) {

        int round = state.getCurrentRound();

        if (round < startRound || round > endRound) return;
        if (appliedRounds.contains(round)) return;

        ScoreTrack score = state.getScoreTrack();

        score.setEconomy(score.getEconomy() + economyEffect);
        score.setEnvironment(score.getEnvironment() + environmentEffect);
        score.setHealth(score.getHealth() + healthEffect);

        appliedRounds.add(round);
    }

    @Override
    public boolean isOngoing() {
        return startRound != endRound;
    }
}
