package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

/**
 * Applies intermediate scoring rules that evaluate the player's logistics
 * setup during the game.
 */
@Service
public class ScoringService {

    /**
     * Scores wasted apples, empty logistics, and perfect balance bonuses.
     *
     * @param state current game state
     * @return score changes and explanation strings for the UI
     */
    public ScoreResult applyIntermediateScoring(GameState state) {
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

        long applesProduced = plantation.getApples().stream()
                .filter(a -> a.getHarvestedRound() == state.getCurrentRound()
                        && (a.getLocation() == AppleLocation.IN_TRANSPORT
                        || a.getLocation() == AppleLocation.WASTED))
                .count();
        long producedApplesInTransport = plantation.getApples().stream()
                .filter(a -> a.getHarvestedRound() == state.getCurrentRound()
                        && a.getLocation() == AppleLocation.IN_TRANSPORT)
                .count();
        long currentlyFreeTransportSpaces = plantation.getCrates().stream()
                .mapToLong(crate -> {
                    long occupied = plantation.getApples().stream()
                            .filter(a -> a.getLocation() == AppleLocation.IN_TRANSPORT
                                    && crate.getId().equals(a.getContainerId()))
                            .count();
                    return Math.max(0, crate.getCapacity() - occupied);
                })
                .sum();
        long transportCapacityAtHarvest =
                currentlyFreeTransportSpaces + producedApplesInTransport;

        result.setWastedApples(Math.toIntExact(wasted));
        result.setApplesProduced(Math.toIntExact(applesProduced));
        result.setTransportCapacity(Math.toIntExact(transportCapacityAtHarvest));
        result.setWasteReason(buildWasteReason(
                wasted, applesProduced, transportCapacityAtHarvest));

        int wastePenalty = (int) (wasted / 3);
        if (wastePenalty > 0) {
            economyChange -= wastePenalty;
            result.addReason("-" + wastePenalty + " Economy (Wasted apples)");
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
                result.addReason("-1 Economy (Empty transport crate)");
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
                result.addReason("-1 Economy (Empty sales stand)");
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
            result.addReason("+1 Economy (Perfect balance bonus)");
        }

        // Set totals in result
        // Apply totals
        score.setEconomy(score.getEconomy() + economyChange);
        score.setEnvironment(score.getEnvironment() + environmentChange);
        score.setHealth(score.getHealth() + healthChange);

        if (score.isGameOver()) {
            state.setGameOver(true);
            state.setGameResult(GameResult.LOSS);
        }

        // set totals into existing result object
        result.setEconomyChange(economyChange);
        result.setEnvironmentChange(environmentChange);
        result.setHealthChange(healthChange);

        return result;


    }

    private String buildWasteReason(
            long wasted, long applesProduced, long transportCapacity) {
        if (applesProduced == 0) {
            return "No apples were produced this round.";
        }
        if (wasted == 0) {
            return "No apples were wasted because the available transport capacity was sufficient.";
        }
        if (transportCapacity == 0) {
            return wasted + " apples were wasted because no transport capacity was available.";
        }
        return wasted + " apples were wasted because " + applesProduced
                + " apples were produced, but only " + transportCapacity
                + " transport spaces were available.";
    }

}
