package com.apfelkomplott.apfelkomplott.engine;

import com.apfelkomplott.apfelkomplott.entity.EventCardDefinition;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.market.EventCardDeck;
import com.apfelkomplott.apfelkomplott.service.*;
import org.springframework.stereotype.Component;

@Component
public class RoundEngine {

    private final SellService sellService;
    private final DeliveryService deliveryService;
    private final HarvestService harvestService;
    private final RotationService rotationService;
    private final ScoringService scoringService;
    private final CardScoringService cardScoringService;

    private final EventCardDeck eventCardDeck;
    private final EventService eventService;


    public RoundEngine(
            SellService sellService,
            DeliveryService deliveryService,
            HarvestService harvestService,
            RotationService rotationService,
            ScoringService scoringService,
            CardScoringService cardScoringService, EventCardDeck eventCardDeck, EventService eventService) {

        this.sellService = sellService;
        this.deliveryService = deliveryService;
        this.harvestService = harvestService;
        this.rotationService = rotationService;
        this.scoringService = scoringService;
        this.cardScoringService = cardScoringService;
        this.eventCardDeck = eventCardDeck;
        this.eventService = eventService;
    }

/*    public void runFullRound(GameState state) {

        // STEP 1
        state.setCurrentPhase(GamePhase.MOVE_MARKER);

        state.getPlantation().resetApplePriceModifier();

        // STEP 2
        state.setCurrentPhase(GamePhase.DRAW_EVENT);

        EventCardDefinition card = eventCardDeck.draw();
        state.getActiveEvents().add(card);
        eventService.applyEvent(state, card);


        // STEP 3
        state.setCurrentPhase(GamePhase.REFILL_CARDS);

        // STEP 4
        state.setCurrentPhase(GamePhase.SELL);
        sellService.sell(state);

        // STEP 5
        state.setCurrentPhase(GamePhase.DELIVER);
        deliveryService.deliver(state);

        // STEP 6 (THIS IS WHAT YOU NEED)
        state.setCurrentPhase(GamePhase.HARVEST);
        harvestService.harvest(state);

        // STEP 7
        state.setCurrentPhase(GamePhase.ROTATE);
        rotationService.rotate(state);

        // STEP 8
        state.setCurrentPhase(GamePhase.INTERMEDIATE_SCORING);
        scoringService.applyIntermediateScoring(state);

        // STEP 9 (UI handles investments)
        state.setCurrentPhase(GamePhase.INVEST);

        // STEP 10
        state.setCurrentPhase(GamePhase.CARD_SCORING);
        cardScoringService.applyCardScoring(state);
    }*/


    public void runNextPhase(GameState state) {

        if (state.isGameOver()) return;

        switch (state.getCurrentPhase()) {

            case MOVE_MARKER -> state.setCurrentPhase(GamePhase.DRAW_EVENT);

            case DRAW_EVENT -> {
                System.out.println(">>> DRAW_EVENT EXECUTED <<<");

                state.getPlantation().resetApplePriceModifier();
                state.getActiveEvents().clear();

                EventCardDefinition card = eventCardDeck.draw();

                if (card != null) {
                    state.getActiveEvents().add(card);
                    eventService.applyEvent(state, card);
                } else {
                    System.out.println("⚠️ No event cards left, skipping event");
                }

                state.setCurrentPhase(GamePhase.REFILL_CARDS);
            }


            case REFILL_CARDS -> state.setCurrentPhase(GamePhase.SELL);

            case SELL -> {
                sellService.sell(state);
                state.setCurrentPhase(GamePhase.DELIVER);
            }

            case DELIVER -> {
                deliveryService.deliver(state);
                state.setCurrentPhase(GamePhase.HARVEST);
            }

            case HARVEST -> {
                harvestService.harvest(state);
                state.setCurrentPhase(GamePhase.ROTATE);
            }

            case ROTATE -> {
                rotationService.rotate(state);
                state.setCurrentPhase(GamePhase.INTERMEDIATE_SCORING);
            }

            case INTERMEDIATE_SCORING -> {
                scoringService.applyIntermediateScoring(state);
                state.setCurrentPhase(GamePhase.INVEST);
            }

            case INVEST -> state.setCurrentPhase(GamePhase.CARD_SCORING);

            case CARD_SCORING -> {
                cardScoringService.applyCardScoring(state);

                if (state.getCurrentRound() >= 15) {
                    state.setGameOver(true);
                    return;
                }

                state.setCurrentRound(state.getCurrentRound() + 1);
                state.setCurrentPhase(GamePhase.MOVE_MARKER);
            }
        }
    }


}
