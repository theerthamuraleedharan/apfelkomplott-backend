package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    public void deliver(GameState state) {

        for (Apple apple : state.getPlantation().getApples()) {

            if (apple.getLocation() != AppleLocation.IN_CRATE) continue;

            // Apple must be at least 1 round old
            if (apple.getHarvestedRound() > state.getCurrentRound() - 1) continue;

            for (SalesStand stand : state.getPlantation().getSalesStands()) {

                long count = state.getPlantation().getApples().stream()
                        .filter(a -> a.getLocation() == AppleLocation.IN_SALES_STAND
                                && stand.getId().equals(a.getContainerId()))
                        .count();

                if (count < stand.getCapacity()) {
                    apple.setLocation(AppleLocation.IN_SALES_STAND);
                    apple.setContainerId(stand.getId());
                    break;
                }
            }
        }
    }
}
