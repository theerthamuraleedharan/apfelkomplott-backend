package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Applies the harvest phase by creating apples from mature trees and placing
 * them into available transport crates.
 */
@Service
public class HarvestService {

    private final EventService eventService;

    public HarvestService(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Harvests apples from mature trees in fields three to six.
     *
     * <p>If transport crates have no remaining capacity, the produced apples are
     * marked as wasted. Temporary harvest-loss effects from event cards are
     * consumed during this operation.</p>
     *
     * @param state game state whose plantation should be harvested
     */
    public void harvest(GameState state) {
        Plantation plantation = state.getPlantation();
        List<Tree> matureTrees = plantation.getTrees().stream()
                .filter(Tree::isMature)
                .toList();
        int harvestLoss = Math.min(matureTrees.size(), eventService.calculateHarvestLoss(state));
        int harvestableCount = Math.max(0, matureTrees.size() - harvestLoss);

        for (int i = 0; i < harvestableCount; i++) {
            Tree tree = matureTrees.get(i);

            if (tree.getFieldPosition() >= 3 && tree.getFieldPosition() <= 6) {
                Apple apple = new Apple();
                apple.setHarvestedRound(state.getCurrentRound());

                boolean placed = placeInTransport(state, apple);

                if (!placed) {
                    apple.setLocation(AppleLocation.WASTED);
                }

                plantation.getApples().add(apple);
            }
        }

        state.getRoundEventImpact().clear();
    }

    private boolean placeInTransport(GameState state, Apple apple) {

        Plantation plantation = state.getPlantation();

        for (Crate crate : plantation.getCrates()) {

            long count = plantation.getApples().stream()
                    .filter(a -> a.getLocation() == AppleLocation.IN_TRANSPORT
                            && crate.getId().equals(a.getContainerId()))
                    .count();

            if (count < crate.getCapacity()) {

                apple.setLocation(AppleLocation.IN_TRANSPORT);
                apple.setContainerId(crate.getId());
                return true;
            }
        }

        return false; // no space -> wasted
    }
}
