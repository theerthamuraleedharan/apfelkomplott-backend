package com.apfelkomplott.apfelkomplott.controller;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.cards.ProductionCardDef;
import com.apfelkomplott.apfelkomplott.controller.dto.BuyProductionRequest;
import com.apfelkomplott.apfelkomplott.controller.dto.InvestmentActionRequest;
import com.apfelkomplott.apfelkomplott.engine.RoundEngine;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.ScoreResult;
import com.apfelkomplott.apfelkomplott.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/game")
public class GameController {

    // Core services used to create, progress, and update the current game session.
    private final GameInitializer gameInitializer;
    private final RoundEngine roundEngine;
    private final InvestmentService investmentService;
    private final GameStateService gameStateService;
    private final ProductionCardService productionCardService;

    private final EventService eventService;

    public GameController(
            GameInitializer gameInitializer,
            RoundEngine roundEngine,
            InvestmentService investmentService,
            GameStateService gameStateService,
            ProductionCardService productionCardService, EventService eventService) {
        this.gameInitializer = gameInitializer;
        this.roundEngine = roundEngine;
        this.investmentService = investmentService;
        this.gameStateService = gameStateService;
        this.productionCardService = productionCardService;
        this.eventService = eventService;
    }

    // ===============================
    // GAME LIFECYCLE
    // ===============================

   /* @PostMapping("/start")
    public GameState startGame() {
        return gameStateService.createNewGame(new GameState());
    }
*/

    @PostMapping("/start")
    public GameState start(@RequestParam FarmingMode mode) {
        // Create a fresh game state with the selected farming mode before persisting it.
        GameState s = new GameState();
        s.setFarmingMode(mode);

        // Prepare the production deck and initial market for the new game.
        productionCardService.initDeckAndMarket(s);

        return gameStateService.createNewGame(s);
    }


    @PostMapping("/start-demo")
    public GameState startDemoGame() {
        // Seed the game with demo data for quicker manual testing.
        GameState state = gameInitializer.createDemoGame();
        return gameStateService.createNewGame(state);
    }

    @GetMapping("/state")
    public GameState getState() {
        // Expose the current saved game state to the frontend.
        GameState state = gameStateService.getState();
        if (state == null) {
            throw new IllegalStateException("Game not started");
        }
        return state;
    }

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


    // ===============================
    // INVESTMENT
    // ===============================

    @PostMapping("/invest")
    public GameState invest(@RequestBody InvestmentActionRequest request) {

        GameState state = gameStateService.getState();
        if (state == null) {
            throw new IllegalStateException("Game not started");
        }

        if (state.isGameOver()) {
            return state;
        }

        // Apply the chosen investment action to the active game.
        investmentService.invest(state, request.getInvestmentType());
        return gameStateService.updateState(state);
    }

    @PostMapping("/invest/production")
    public GameState buyProduction(@RequestBody BuyProductionRequest req) {
        GameState state = gameStateService.getState();
        // Buy a production card from the current market, then store the updated game state.
        productionCardService.buyCard(state, req.getCardId());
        return gameStateService.updateState(state);
    }


    // ===============================
    // MARKET
    // ===============================

 /*   @GetMapping("/market")
    public List<ProductionCardDef> getMarket() {
        return productionCardService.getVisibleCards();
    }*/
 @GetMapping("/market")
 public List<ProductionCardDef> market() {
     // Return the currently visible production cards for the active game.
     GameState state = gameStateService.getState();
     return productionCardService.getMarketCards(state);
 }

    @PostMapping("/buy-card")
    public ScoreResult buyCard(@RequestParam String cardId) {
        // Purchase a card by id and return the resulting score update payload.
        GameState state = gameStateService.getState();
        return productionCardService.buyCard(state, cardId);
    }

    @PostMapping("/card-scoring")
    public ScoreResult applyCardScoring() {
        GameState state = gameStateService.getState();
        return productionCardService.applyLongTermCardScoring(state);
    }
}
