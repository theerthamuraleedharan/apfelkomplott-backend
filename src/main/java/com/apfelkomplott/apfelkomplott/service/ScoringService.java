package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

@Service
public class ScoringService {

    public ScoreResult applyIntermediateScoring(GameState state) {

        if (state.getCurrentRound() < 3) {
            return new ScoreResult(0, 0, 0);
        }

        Plantation plantation = state.getPlantation();
        ScoreTrack score = state.getScoreTrack();

        int economyChange = 0;
        int environmentChange = 0;
        int healthChange = 0;

        ScoreResult result = new ScoreResult(0, 0, 0);

        // 1️⃣ WASTED APPLES
        long wasted = plantation.getApples().stream()
                .filter(a -> a.getLocation() == AppleLocation.WASTED)
                .count();

        int wastePenalty = (int) (wasted / 3);
        if (wastePenalty > 0) {
            economyChange -= wastePenalty;
        }

        plantation.getApples().removeIf(
                a -> a.getLocation() == AppleLocation.WASTED
        );

        // 2️⃣ EMPTY CRATES
        for (Crate crate : plantation.getCrates()) {

            long count = plantation.getApples().stream()
                    .filter(a -> a.getLocation() == AppleLocation.IN_TRANSPORT
                            && crate.getId().equals(a.getContainerId()))
                    .count();

            if (count == 0) {
                economyChange -= 1;
            }
        }

        // 3️⃣ EMPTY SALES STANDS
        for (SalesStand stand : plantation.getSalesStands()) {

            long count = plantation.getApples().stream()
                    .filter(a -> a.getLocation() == AppleLocation.IN_SALES_STAND
                            && stand.getId().equals(a.getContainerId()))
                    .count();

            if (count == 0) {
                economyChange -= 1;
            }
        }

        // 4️⃣ PERFECT BONUS
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

        // Set totals in result
        // Apply totals
        score.setEconomy(score.getEconomy() + economyChange);
        score.setEnvironment(score.getEnvironment() + environmentChange);
        score.setHealth(score.getHealth() + healthChange);

        if (score.isGameOver()) {
            state.setGameOver(true);
        }

        // set totals into existing result object
        result.setEconomyChange(economyChange);
        result.setEnvironmentChange(environmentChange);
        result.setHealthChange(healthChange);

        return result;


    }

}
