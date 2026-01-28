package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

@Service
public class SellService {

    public void sell(GameState state) {

        for (Apple apple : state.getPlantation().getApples()) {

            // Only apples in sales stands can be sold
            if (apple.getLocation() != AppleLocation.IN_SALES_STAND) {
                continue;
            }

            // Enforce 2-round delay:
            // Harvested in round N → sold in round N+2
            if (apple.getHarvestedRound() > state.getCurrentRound() - 2) {
                continue;
            }

            // Sell apple
            apple.setLocation(AppleLocation.SOLD);
            apple.setContainerId(null); // no longer in a stand
            state.setMoney(state.getMoney() + 1);
        }
    }
}
