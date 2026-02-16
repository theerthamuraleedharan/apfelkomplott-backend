package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class SellService {

    public void sell(GameState state) {

        Plantation plantation = state.getPlantation();

        int basePrice = 1;
        int modifier = plantation.getApplePriceModifier();

        Iterator<Apple> iterator = plantation.getApples().iterator();

        int soldCount = 0;

        while (iterator.hasNext()) {

            Apple apple = iterator.next();

            if (apple.getLocation() == AppleLocation.IN_SALES_STAND) {

                int finalPrice = basePrice + modifier;
                state.setMoney(state.getMoney() + finalPrice);

                soldCount++;
                iterator.remove();
            }
        }

        System.out.println("Sold apples: " + soldCount);
        System.out.println("Money after sell: " + state.getMoney());
    }
}



