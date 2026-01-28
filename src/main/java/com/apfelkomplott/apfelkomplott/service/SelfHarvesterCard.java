package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.ProductionCard;

import java.util.HashSet;
import java.util.Set;

public class SelfHarvesterCard implements ProductionCard {

    private final Set<Integer> appliedRounds = new HashSet<>();

    @Override
    public String getName() {
        return "SELF_HARVESTER";
    }

    @Override
    public void apply(GameState state) {
        int round = state.getCurrentRound();

        if (round >= 1 && round <= 2 && !appliedRounds.contains(round)) {

            state.getScoreTrack().setEconomy(
                state.getScoreTrack().getEconomy() + 1
            );

            appliedRounds.add(round);
        }
    }

    @Override
    public boolean isOngoing() {
        return true;
    }
}
