package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {

    public void applyIntermediateScoring(GameState state) {

        ScoreTrack score = state.getScoreTrack();

        // Empty crates
        for (Crate crate : state.getPlantation().getCrates()) {
            boolean empty = state.getPlantation().getApples().stream()
                    .noneMatch(a -> a.getLocation() == AppleLocation.IN_CRATE
                                && a.getContainerId().equals(crate.getId()));

            if (empty) score.setEconomy(score.getEconomy() - 1);
        }

        // Empty sales stands
        for (SalesStand stand : state.getPlantation().getSalesStands()) {
            boolean empty = state.getPlantation().getApples().stream()
                    .noneMatch(a -> a.getLocation() == AppleLocation.IN_SALES_STAND
                                && a.getContainerId().equals(stand.getId()));

            if (empty) score.setEconomy(score.getEconomy() - 1);
        }

        // Wasted apples
        long wasted = state.getPlantation().getApples().stream()
                .filter(a -> a.getLocation() == AppleLocation.WASTED)
                .count();

        score.setEconomy(score.getEconomy() - (int)(wasted / 3));

        // Game over check
        if (score.isGameOver()) {
            state.setGameOver(true);
        }
    }
}
