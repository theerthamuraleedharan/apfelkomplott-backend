package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.controller.dto.SellResult;
import com.apfelkomplott.apfelkomplott.entity.Apple;
import com.apfelkomplott.apfelkomplott.entity.AppleLocation;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SellServiceTests {

    private final SellService service = new SellService();

    @Test
    void sellRemovesOnlySalesStandApplesAndAppliesPriceModifier() {
        GameState state = new GameState();
        state.setMoney(5);
        state.getPlantation().setApplePriceModifier(2);
        addApple(state, AppleLocation.IN_SALES_STAND);
        addApple(state, AppleLocation.IN_SALES_STAND);
        addApple(state, AppleLocation.IN_TRANSPORT);
        addApple(state, AppleLocation.WASTED);

        SellResult result = service.sell(state);

        assertEquals(2, result.getApplesSold());
        assertEquals(6, result.getMoneyEarned());
        assertEquals(11, state.getMoney());
        assertEquals(2, state.getPlantation().getApples().size());
        assertFalse(state.getPlantation().getApples().stream()
                .anyMatch(apple -> apple.getLocation() == AppleLocation.IN_SALES_STAND));
    }

    private void addApple(GameState state, AppleLocation location) {
        Apple apple = new Apple();
        apple.setLocation(location);
        state.getPlantation().getApples().add(apple);
    }
}
