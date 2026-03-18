package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.controller.dto.SellResult;
import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class SellService {

    public SellResult sell(GameState state) {

        Plantation plantation = state.getPlantation();

        // Every apple starts with the same base sale price.
        // Permanent sale-boosting effects, such as the Premium event card,
        // are accumulated in plantation.applePriceModifier and increase the
        // price of every apple sold in this and future rounds.
        int basePrice = 1;
        int modifier = plantation.getApplePriceModifier();

        Iterator<Apple> iterator = plantation.getApples().iterator();

        int soldCount = 0;
        int totalEarned = 0;

        while (iterator.hasNext()) {

            Apple apple = iterator.next();

            if (apple.getLocation() == AppleLocation.IN_SALES_STAND) {

                // Final price per sold apple:
                //   base price (always 1)
                // + active plantation sale bonus (can stack from repeated events)
                //
                // Example:
                //   Premium drawn twice -> modifier = 2
                //   1 base + 2 bonus = 3 money per sold apple
                int finalPrice = basePrice + modifier;

                totalEarned += finalPrice;
                soldCount++;

                // Once the apple is sold, remove it from the plantation inventory
                // so it cannot be delivered or sold again in later phases.
                iterator.remove();
            }
        }

        // The total earned money is added once after all apples in sales stands
        // have been processed. This keeps the round result easy to report in the UI.
        state.setMoney(state.getMoney() + totalEarned);

        return new SellResult(soldCount, totalEarned);
    }
}



