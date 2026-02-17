package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.controller.dto.SellResult;
import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class SellService {

    public SellResult sell(GameState state) {

        Plantation plantation = state.getPlantation();

        int basePrice = 1;
        int modifier = plantation.getApplePriceModifier();

        Iterator<Apple> iterator = plantation.getApples().iterator();

        int soldCount = 0;
        int totalEarned = 0;

        while (iterator.hasNext()) {

            Apple apple = iterator.next();

            if (apple.getLocation() == AppleLocation.IN_SALES_STAND) {

                int finalPrice = basePrice + modifier;

                totalEarned += finalPrice;
                soldCount++;

                iterator.remove();
            }
        }

        state.setMoney(state.getMoney() + totalEarned);

        return new SellResult(soldCount, totalEarned);
    }
}



