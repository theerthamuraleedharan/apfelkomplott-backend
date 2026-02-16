package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

@Service
public class HarvestService {

    public void harvest(GameState state) {

        Plantation plantation = state.getPlantation();

        for (Tree tree : plantation.getTrees()) {

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
