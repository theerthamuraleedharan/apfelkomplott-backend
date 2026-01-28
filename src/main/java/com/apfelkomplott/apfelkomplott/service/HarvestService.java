package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

@Service
public class HarvestService {

    public void harvest(GameState state) {

        for (Tree tree : state.getPlantation().getTrees()) {

            if (tree.isMature()) {

                Apple apple = new Apple();
                apple.setHarvestedRound(state.getCurrentRound());

                boolean placed = placeInCrate(state, apple);

                if (!placed) {
                    apple.setLocation(AppleLocation.WASTED);
                }

                state.getPlantation().getApples().add(apple);
            }
        }
    }

    private boolean placeInCrate(GameState state, Apple apple) {

        for (Crate crate : state.getPlantation().getCrates()) {

            long count = state.getPlantation().getApples().stream()
                    .filter(a -> a.getLocation() == AppleLocation.IN_CRATE
                            && crate.getId().equals(a.getContainerId()))
                    .count();

            if (count < crate.getCapacity()) {
                apple.setLocation(AppleLocation.IN_CRATE);
                apple.setContainerId(crate.getId());
                return true;
            }
        }
        return false;
    }
}
