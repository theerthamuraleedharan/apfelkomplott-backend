package com.apfelkomplott.apfelkomplott.market;

import com.apfelkomplott.apfelkomplott.entity.ProductionCardDefinition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class ProductionMarket {

    private final ProductionCardDeck deck;
    private final List<ProductionCardDefinition> visibleCards = new ArrayList<>();

    public ProductionMarket(ProductionCardDeck deck) {
        this.deck = deck;
        refill();
    }

    /**
     * Always returns exactly 5 visible cards.
     */
    public List<ProductionCardDefinition> getVisibleCards() {
        return visibleCards;
    }

    /**
     * Player takes a card from the market.
     * The card is removed and immediately replaced.
     */
    public ProductionCardDefinition take(String cardName) {

        ProductionCardDefinition taken = visibleCards.stream()
                .filter(c -> c.getName().equals(cardName))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException("Card not found in market: " + cardName));

        visibleCards.remove(taken);
        visibleCards.add(deck.draw());

        return taken;
    }

    /**
     * Used if purchase fails (e.g. not enough money).
     */
    public void returnCard(ProductionCardDefinition card) {
        visibleCards.remove(visibleCards.size() - 1);
        visibleCards.add(card);
    }

    private void refill() {
        while (visibleCards.size() < 5) {
            visibleCards.add(deck.draw());
        }
    }
}
