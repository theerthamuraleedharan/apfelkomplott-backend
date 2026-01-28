package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {

    public void applyIntermediateScoring(GameState state) {

        // 📄 Rule: scoring only from round 3 onward
        if (state.getCurrentRound() < 3) {
            return;
        }

        ScoreTrack score = state.getScoreTrack();
        Plantation p = state.getPlantation();

        int penalty = 0;

        // 1️⃣ Wasted apples
        long wasted = p.getApples().stream()
                .filter(a -> a.getLocation() == AppleLocation.WASTED)
                .count();

        penalty += (int) (wasted / 3);

        // 🔥 remove wasted apples so they are not counted again
        p.getApples().removeIf(a -> a.getLocation() == AppleLocation.WASTED);

        // 2️⃣ Empty crates
        for (Crate crate : p.getCrates()) {
            boolean empty = p.getApples().stream()
                    .noneMatch(a ->
                            a.getLocation() == AppleLocation.IN_CRATE &&
                                    crate.getId().equals(a.getContainerId())
                    );

            if (empty) penalty++;
        }

        // 3️⃣ Empty sales stands
        for (SalesStand stand : p.getSalesStands()) {
            boolean empty = p.getApples().stream()
                    .noneMatch(a ->
                            a.getLocation() == AppleLocation.IN_SALES_STAND &&
                                    stand.getId().equals(a.getContainerId())
                    );

            if (empty) penalty++;
        }

        // 4️⃣ PERFECT BALANCE BONUS (+1)
        boolean allCratesFull = p.getCrates().stream().allMatch(crate ->
                p.getApples().stream()
                        .filter(a -> a.getLocation() == AppleLocation.IN_CRATE
                                && crate.getId().equals(a.getContainerId()))
                        .count() == crate.getCapacity()
        );

        boolean allStandsFull = p.getSalesStands().stream().allMatch(stand ->
                p.getApples().stream()
                        .filter(a -> a.getLocation() == AppleLocation.IN_SALES_STAND
                                && stand.getId().equals(a.getContainerId()))
                        .count() == stand.getCapacity()
        );

        if (allCratesFull && allStandsFull && !p.getCrates().isEmpty() && !p.getSalesStands().isEmpty()) {
            score.setEconomy(score.getEconomy() + 1);
        }

        score.setEconomy(score.getEconomy() - penalty);

        if (score.isGameOver()) {
            state.setGameOver(true);
        }
    }
}
