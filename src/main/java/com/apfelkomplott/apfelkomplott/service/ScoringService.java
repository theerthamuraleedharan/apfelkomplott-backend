package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {

    public void applyIntermediateScoring(GameState state) {

        //  Only from round 3 onward
        if (state.getCurrentRound() < 3) return;

        Plantation plantation = state.getPlantation();
        ScoreTrack score = state.getScoreTrack();

        int economyChange = 0;

        // =========================
        // 1️⃣ WASTED APPLES PENALTY
        // =========================

        long wasted = plantation.getApples().stream()
                .filter(a -> a.getLocation() == AppleLocation.WASTED)
                .count();

        int wastePenalty = (int) (wasted / 3);
        economyChange -= wastePenalty;

        // Remove wasted apples so they don't count again
        plantation.getApples().removeIf(
                a -> a.getLocation() == AppleLocation.WASTED
        );

        // =========================
        // 2️⃣ EMPTY CRATES PENALTY
        // =========================

        for (Crate crate : plantation.getCrates()) {

            long count = plantation.getApples().stream()
                    .filter(a -> a.getLocation() == AppleLocation.IN_TRANSPORT
                            && crate.getId().equals(a.getContainerId()))
                    .count();

            if (count == 0) {
                economyChange -= 1;
            }
        }

        // =========================
        // 3️⃣ EMPTY SALES STANDS PENALTY
        // =========================

        for (SalesStand stand : plantation.getSalesStands()) {

            long count = plantation.getApples().stream()
                    .filter(a -> a.getLocation() == AppleLocation.IN_SALES_STAND
                            && stand.getId().equals(a.getContainerId()))
                    .count();

            if (count == 0) {
                economyChange -= 1;
            }
        }

        // =========================
        // 4️⃣ PERFECT BALANCE BONUS
        // =========================

        boolean allTransportFull = plantation.getCrates().stream()
                .allMatch(crate -> plantation.getApples().stream()
                        .filter(a -> a.getLocation() == AppleLocation.IN_TRANSPORT
                                && crate.getId().equals(a.getContainerId()))
                        .count() == crate.getCapacity());

        boolean allSalesFull = plantation.getSalesStands().stream()
                .allMatch(stand -> plantation.getApples().stream()
                        .filter(a -> a.getLocation() == AppleLocation.IN_SALES_STAND
                                && stand.getId().equals(a.getContainerId()))
                        .count() == stand.getCapacity());

        if (allTransportFull && allSalesFull && wasted == 0
                && !plantation.getCrates().isEmpty()
                && !plantation.getSalesStands().isEmpty()) {

            economyChange += 1;
        }

        // =========================
        // APPLY RESULT
        // =========================

        score.setEconomy(score.getEconomy() + economyChange);

        // Game over check (if your ScoreTrack has thresholds)
        if (score.isGameOver()) {
            state.setGameOver(true);
        }
    }
}
