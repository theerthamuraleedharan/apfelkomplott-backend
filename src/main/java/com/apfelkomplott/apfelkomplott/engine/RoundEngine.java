package com.apfelkomplott.apfelkomplott.engine;

import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameState;
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

    public RoundEngine(
            SellService sellService,
            DeliveryService deliveryService,
            HarvestService harvestService,
            RotationService rotationService,
            ScoringService scoringService,
            CardScoringService cardScoringService) {

        this.sellService = sellService;
        this.deliveryService = deliveryService;
        this.harvestService = harvestService;
        this.rotationService = rotationService;
        this.scoringService = scoringService;
        this.cardScoringService = cardScoringService;
    }

    public void runFullRound(GameState state) {

        // STEP 1
        state.setCurrentPhase(GamePhase.MOVE_MARKER);

        // STEP 2
        state.setCurrentPhase(GamePhase.DRAW_EVENT);

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
    }


    public void runNextPhase(GameState state) {

        if (state.isGameOver()) {
            return; // absolutely nothing happens
        }


        switch (state.getCurrentPhase()) {

            case MOVE_MARKER -> state.setCurrentPhase(GamePhase.DRAW_EVENT);

            case DRAW_EVENT -> state.setCurrentPhase(GamePhase.REFILL_CARDS);

            case REFILL_CARDS -> {
                state.setCurrentPhase(GamePhase.SELL);
                sellService.sell(state);
            }

            case SELL -> {
                state.setCurrentPhase(GamePhase.DELIVER);
                deliveryService.deliver(state);
            }

            case DELIVER -> {
                state.setCurrentPhase(GamePhase.HARVEST);
                harvestService.harvest(state);
            }

            case HARVEST -> {
                state.setCurrentPhase(GamePhase.ROTATE);
                rotationService.rotate(state);
            }

            case ROTATE -> {
                state.setCurrentPhase(GamePhase.INTERMEDIATE_SCORING);
                scoringService.applyIntermediateScoring(state);
            }

            case INTERMEDIATE_SCORING -> state.setCurrentPhase(GamePhase.INVEST);

            case INVEST -> {
                state.setCurrentPhase(GamePhase.CARD_SCORING);
                cardScoringService.applyCardScoring(state);
            }

            case CARD_SCORING -> {
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
