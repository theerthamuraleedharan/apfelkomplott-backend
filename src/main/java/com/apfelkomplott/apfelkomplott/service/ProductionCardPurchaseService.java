package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.ProductionCardDefinition;
import com.apfelkomplott.apfelkomplott.market.ProductionMarket;
import org.springframework.stereotype.Service;

@Service
public class ProductionCardPurchaseService {

    private final ProductionMarket productionMarket;
    private final ProductionCardFactory cardFactory;

    public ProductionCardPurchaseService(
            ProductionMarket productionMarket,
            ProductionCardFactory cardFactory) {
        this.productionMarket = productionMarket;
        this.cardFactory = cardFactory;
    }

    public void buyProductionCard(GameState state, String cardName) {

        if (state.isGameOver()) return;

        boolean alreadyOwned = state.getActiveProductionCards().stream()
                .anyMatch(c -> c.getName().equals(cardName));

        if (alreadyOwned) return;

        ProductionCardDefinition def = productionMarket.take(cardName);

        if (state.getMoney() < def.getCost()) {
            productionMarket.returnCard(def);
            return;
        }

        state.getActiveProductionCards().add(cardFactory.create(def));
        state.setMoney(state.getMoney() - def.getCost());
    }
}
