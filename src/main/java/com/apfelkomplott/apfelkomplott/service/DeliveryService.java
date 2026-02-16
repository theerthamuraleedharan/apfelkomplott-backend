package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryService {

    public void deliver(GameState state) {

        Plantation plantation = state.getPlantation();

        List<Apple> apples = plantation.getApples();

        for (SalesStand stand : plantation.getSalesStands()) {

            long standCount = apples.stream()
                    .filter(a -> a.getLocation() == AppleLocation.IN_SALES_STAND
                            && stand.getId().equals(a.getContainerId()))
                    .count();

            for (Apple apple : apples) {

                if (standCount >= stand.getCapacity())
                    break;

                if (apple.getLocation() == AppleLocation.IN_TRANSPORT) {

                    apple.setLocation(AppleLocation.IN_SALES_STAND);
                    apple.setContainerId(stand.getId());

                    standCount++;
                }
            }
        }
    }
}
