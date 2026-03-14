package com.apfelkomplott.apfelkomplott.controller;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.cards.ProductionCardDef;
import com.apfelkomplott.apfelkomplott.controller.dto.ActiveLongTermCardView;
import com.apfelkomplott.apfelkomplott.controller.dto.BuyProductionRequest;
import com.apfelkomplott.apfelkomplott.controller.dto.InvestmentActionRequest;
import com.apfelkomplott.apfelkomplott.engine.RoundEngine;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.service.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/game")
public class GameController {

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
        GameState s = new GameState();
        s.setFarmingMode(mode);

        productionCardService.initDeckAndMarket(s);

        return gameStateService.createNewGame(s);
    }


    @PostMapping("/start-demo")
    public GameState startDemoGame() {
        GameState state = gameInitializer.createDemoGame();
        return gameStateService.createNewGame(state);
    }

    @GetMapping("/state")
    public GameState getState() {
        GameState state = gameStateService.getState();
        if (state == null) {
            throw new IllegalStateException("Game not started");
        }
        return state;
    }

    @PostMapping("/next-phase")
    public GameState nextPhase() {

        GameState state = gameStateService.getState();

        if (state == null || state.isGameOver()) {
            return state;
        }

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

        investmentService.invest(state, request.getInvestmentType());
        return gameStateService.updateState(state);
    }

    @PostMapping("/invest/production")
    public GameState buyProduction(@RequestBody BuyProductionRequest req) {
        GameState state = gameStateService.getState();
        productionCardService.buyCard(state, req);
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
     GameState state = gameStateService.getState();
     return productionCardService.getMarketCards(state);
 }

    @GetMapping("/active-long-term")
    public List<ActiveLongTermCardView> getActiveLongTerm() {
        GameState state = gameStateService.getState();
        return productionCardService.getActiveLongTermView(state);
    }
}
