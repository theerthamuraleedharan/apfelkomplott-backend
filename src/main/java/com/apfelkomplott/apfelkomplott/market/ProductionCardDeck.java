package com.apfelkomplott.apfelkomplott.market;

import com.apfelkomplott.apfelkomplott.entity.CardCategory;
import com.apfelkomplott.apfelkomplott.entity.ProductionCardDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

@Component
public class ProductionCardDeck {

    private final Stack<ProductionCardDefinition> deck = new Stack<>();

    public ProductionCardDeck() {

        List<ProductionCardDefinition> cards = new ArrayList<>(List.of(

            // ===== PRODUCTION / ECONOMY =====
            new ProductionCardDefinition(
                "SELF_HARVESTER",
                5,
                CardCategory.PRODUCTION,
                1, 0, 0,
                1, 2,
                "Increases economy in early rounds"
            ),

            new ProductionCardDefinition(
                "ADVANCED_HARVESTER",
                10,
                CardCategory.PRODUCTION,
                2, 0, 0,
                2, 4,
                "Strong economic boost in mid game"
            ),

            // ===== ENVIRONMENT =====
            new ProductionCardDefinition(
                "WATER_MANAGEMENT",
                10,
                CardCategory.ENVIRONMENT,
                0, 1, 0,
                1, 3,
                "Improves water efficiency and sustainability"
            ),

            new ProductionCardDefinition(
                "BIODIVERSITY",
                12,
                CardCategory.ENVIRONMENT,
                0, 2, 0,
                2, 4,
                "Supports biodiversity and environment"
            ),

            // ===== HEALTH =====
            new ProductionCardDefinition(
                "SHADE_NETS",
                8,
                CardCategory.HEALTH,
                0, 0, 1,
                1, 3,
                "Protects trees from heat stress"
            ),

            // ===== MIXED =====
            new ProductionCardDefinition(
                "PEST_CONTROL",
                12,
                CardCategory.MIXED,
                0, -1, 1,
                1, 2,
                "Improves health but harms environment"
            ),

            new ProductionCardDefinition(
                "OPTIMIZE_HARVEST",
                15,
                CardCategory.MIXED,
                1, 1, 1,
                2, 2,
                "One-time optimization of harvest process"
            )
        ));

        Collections.shuffle(cards);
        deck.addAll(cards);
    }

    public ProductionCardDefinition draw() {
        if (deck.isEmpty()) {
            throw new IllegalStateException("Production card deck is empty");
        }
        return deck.pop();
    }
}
