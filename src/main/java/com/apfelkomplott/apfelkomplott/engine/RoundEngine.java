package com.apfelkomplott.apfelkomplott.engine;

import com.apfelkomplott.apfelkomplott.controller.dto.SellResult;
import com.apfelkomplott.apfelkomplott.entity.EventCard;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.ScoreResult;
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

    private final ProductionCardService productionCardService;


    public RoundEngine(
            SellService sellService,
            DeliveryService deliveryService,
            HarvestService harvestService,
            RotationService rotationService,
            ScoringService scoringService,
            CardScoringService cardScoringService, EventCardDeck eventCardDeck, EventService eventService, ProductionCardService productionCardService) {

        this.sellService = sellService;
        this.deliveryService = deliveryService;
        this.harvestService = harvestService;
        this.rotationService = rotationService;
        this.scoringService = scoringService;
        this.cardScoringService = cardScoringService;
        this.eventCardDeck = eventCardDeck;
        this.eventService = eventService;
        this.productionCardService = productionCardService;
    }

    public void runNextPhase(GameState state) {

        if (state.isGameOver()) return;

        switch (state.getCurrentPhase()) {

            case MOVE_MARKER -> {
                if (state.getCurrentRound() >= 15) {
                    state.setGameOver(true);
                    return;
                }
                state.setCurrentPhase(GamePhase.DRAW_EVENT);
                state.setLastSellResult(null);
                state.setLastScoreResult(null);

            }


            case DRAW_EVENT -> {

                EventCard drawnCard = eventService.drawEvent(state);

                state.setLastDrawnEvent(drawnCard); // for UI popup

                state.setCurrentPhase(GamePhase.REFILL_CARDS);
            }


            case REFILL_CARDS -> {
                productionCardService.refillMarketToFive(state);
                state.setCurrentPhase(GamePhase.SELL);
            }


            case SELL -> {

                SellResult result = sellService.sell(state);
                state.setLastSellResult(result);

                state.setCurrentPhase(GamePhase.DELIVER);
            }


            case DELIVER -> {

                if (state.getCurrentRound() >= 4) {
                    deliveryService.deliver(state);
                }

                state.setCurrentPhase(GamePhase.HARVEST);
            }

            case HARVEST -> {

                if (state.getCurrentRound() >= 3) {
                    harvestService.harvest(state);
                }

                state.setCurrentPhase(GamePhase.ROTATE);
            }

            case ROTATE -> {
                rotationService.rotate(state);
                state.setCurrentPhase(GamePhase.INTERMEDIATE_SCORING);
            }

            case INTERMEDIATE_SCORING -> {

                // If scoring not yet calculated
                if (state.getLastScoreResult() == null
                        && state.getCurrentRound() >= 3) {

                    ScoreResult result =
                            scoringService.applyIntermediateScoring(state);

                    state.setLastScoreResult(result);

                    // STOP here so UI can show popup
                    return;
                }

                // If already calculated → move forward
                state.setCurrentPhase(GamePhase.INVEST);
            }

            case INVEST ->
                    state.setCurrentPhase(GamePhase.CARD_SCORING);


            case CARD_SCORING -> {
               productionCardService.applyLongTermCardScoring(state);
                state.setCurrentRound(state.getCurrentRound() + 1);
                state.setCurrentPhase(GamePhase.MOVE_MARKER);
            }
        }
    }
}
