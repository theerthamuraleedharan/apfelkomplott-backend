package com.apfelkomplott.apfelkomplott.controller;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.cards.ProductionCardDef;
import com.apfelkomplott.apfelkomplott.controller.dto.BuyProductionRequest;
import com.apfelkomplott.apfelkomplott.controller.dto.EventSelectionRequest;
import com.apfelkomplott.apfelkomplott.controller.dto.GameGuideDto;
import com.apfelkomplott.apfelkomplott.controller.dto.HiddenEventCardDto;
import com.apfelkomplott.apfelkomplott.controller.dto.InvestmentActionRequest;
import com.apfelkomplott.apfelkomplott.controller.dto.PhaseHelpDto;
import com.apfelkomplott.apfelkomplott.engine.RoundEngine;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.ScoreResult;
import com.apfelkomplott.apfelkomplott.service.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Exposes the HTTP API used by the web frontend to start a game, advance game
 * phases, buy cards, choose events, and inspect the current game state.
 */
@RestController
@RequestMapping("/game")
public class GameController {

    // Core services used to create, progress, and update the current game session.
    private final GameInitializer gameInitializer;
    private final RoundEngine roundEngine;
    private final InvestmentService investmentService;
    private final GameStateService gameStateService;
    private final ProductionCardService productionCardService;
    private final EventService eventService;
    private final GameHelpService gameHelpService;

    public GameController(
            GameInitializer gameInitializer,
            RoundEngine roundEngine,
            InvestmentService investmentService,
            GameStateService gameStateService,
            ProductionCardService productionCardService,
            EventService eventService,
            GameHelpService gameHelpService) {
        this.gameInitializer = gameInitializer;
        this.roundEngine = roundEngine;
        this.investmentService = investmentService;
        this.gameStateService = gameStateService;
        this.productionCardService = productionCardService;
        this.eventService = eventService;
        this.gameHelpService = gameHelpService;
    }

    /**
     * Starts a new game with the selected farming mode and initializes both card
     * decks before storing the state.
     *
     * @param mode initial farming mode chosen by the player
     * @return initialized game state
     */
    @PostMapping("/start")
    public GameState start(@RequestParam FarmingMode mode) {
        // Create a fresh game state with the selected farming mode before persisting it.
        GameState s = new GameState();
        s.setFarmingMode(mode);

        // Prepare the production deck and initial market for the new game.
        productionCardService.initDeckAndMarket(s);
        eventService.initDeck(s);

        return gameStateService.createNewGame(s);
    }


    /**
     * Returns the active game state.
     *
     * @return current game state
     */
    @GetMapping("/state")
    public GameState getState() {
        // Expose the current saved game state to the frontend.
        return requireState();
    }

    @GetMapping("/help")
    public GameGuideDto getGuide() {
        return gameHelpService.buildGuide();
    }

    @GetMapping("/help/current-phase")
    public PhaseHelpDto getCurrentPhaseHelp() {
        return gameHelpService.buildCurrentPhaseHelp(gameStateService.getState());
    }

    /**
     * Advances the game by one phase through the round engine and stores the
     * updated state.
     *
     * @return updated game state, or the unchanged state if the game is over
     */
    @PostMapping("/next-phase")
    public GameState nextPhase() {

        GameState state = gameStateService.getState();

        // If there is no active game or the game already ended, do not advance anything.
        if (state == null || state.isGameOver()) {
            return state;
        }

        // Delegate phase progression to the round engine, then persist the updated state.
        roundEngine.runNextPhase(state);
        return gameStateService.updateState(state);
    }

    @GetMapping("/event/options")
    public List<HiddenEventCardDto> getEventOptions() {
        GameState state = requireState();
        return eventService.getHiddenOptions(state);
    }

    /**
     * Applies the selected hidden event card and moves the game into the next
     * phase.
     *
     * @param request selected hidden event option
     * @return updated game state after resolving the event
     */
    @PostMapping("/event/select")
    public GameState selectEvent(@Valid @RequestBody EventSelectionRequest request) {
        GameState state = requireState();
        eventService.selectEvent(state, request.getOptionIndex());
        return gameStateService.updateState(state);
    }


    // ===============================
    // INVESTMENT
    // ===============================

    /**
     * Applies one investment action, such as buying a tree, crate, or sales
     * stand.
     *
     * @param request investment action selected by the player
     * @return updated game state
     */
    @PostMapping("/invest")
    public GameState invest(@Valid @RequestBody InvestmentActionRequest request) {

        GameState state = requireState();

        if (state.isGameOver()) {
            return state;
        }

        // Apply the chosen investment action to the active game.
        investmentService.invest(state, request.getInvestmentType());
        return gameStateService.updateState(state);
    }

    /**
     * Buys a production card from the visible market row.
     *
     * @param req card purchase request containing the selected card id
     * @return updated game state after the purchase
     */
    @PostMapping("/invest/production")
    public GameState buyProduction(@Valid @RequestBody BuyProductionRequest req) {
        GameState state = requireState();
        // Buy a production card from the current market, then store the updated game state.
        productionCardService.buyCard(state, req.getCardId());
        return gameStateService.updateState(state);
    }


    // ===============================
    // MARKET
    // ===============================
 @GetMapping("/market")
 public List<ProductionCardDef> market() {
     // Return the currently visible production cards for the active game.
     GameState state = requireState();
     return productionCardService.getMarketCards(state);
 }

    @GetMapping("/active-production-cards")
    public List<ProductionCardDef> activeProductionCards() {
        // Return the long-term production cards that are currently active in the game.
        GameState state = requireState();
        return productionCardService.getActiveProductionCards(state);
    }

    @PostMapping("/buy-card")
    public ScoreResult buyCard(@RequestParam String cardId) {
        // Purchase a card by id and return the resulting score update payload.
        GameState state = requireState();
        ScoreResult result = productionCardService.buyCard(state, cardId);
        gameStateService.updateState(state);
        return result;
    }

    @PostMapping("/card-scoring")
    public ScoreResult applyCardScoring() {
        GameState state = requireState();
        return productionCardService.applyLongTermCardScoring(state);
    }

    private GameState requireState() {
        GameState state = gameStateService.getState();
        if (state == null) {
            throw new IllegalStateException("Game not started");
        }
        return state;
    }
}

