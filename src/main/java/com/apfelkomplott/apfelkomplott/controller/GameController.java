package com.apfelkomplott.apfelkomplott.controller;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.controller.dto.InvestmentActionRequest;
import com.apfelkomplott.apfelkomplott.controller.dto.ProductionCardPurchaseRequest;
import com.apfelkomplott.apfelkomplott.engine.RoundEngine;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.ProductionCardDefinition;
import com.apfelkomplott.apfelkomplott.market.ProductionMarket;
import com.apfelkomplott.apfelkomplott.service.GameInitializer;
import com.apfelkomplott.apfelkomplott.service.GameStateService;
import com.apfelkomplott.apfelkomplott.service.InvestmentService;
import com.apfelkomplott.apfelkomplott.service.ProductionCardPurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/game")
public class GameController {

    private final GameInitializer gameInitializer;
    private final RoundEngine roundEngine;
    private final InvestmentService investmentService;
    private final ProductionMarket productionMarket;
    private final GameStateService gameStateService;
    private final ProductionCardPurchaseService purchaseService;

    public GameController(
            GameInitializer gameInitializer,
            RoundEngine roundEngine,
            InvestmentService investmentService,
            ProductionMarket productionMarket,
            GameStateService gameStateService,
            ProductionCardPurchaseService purchaseService
    ) {
        this.gameInitializer = gameInitializer;
        this.roundEngine = roundEngine;
        this.investmentService = investmentService;
        this.productionMarket = productionMarket;
        this.gameStateService = gameStateService;
        this.purchaseService = purchaseService;
    }

    // ===============================
    // GAME LIFECYCLE
    // ===============================

    @PostMapping("/start")
    public GameState startGame() {
        return gameStateService.createNewGame(new GameState());
    }

    @PostMapping("/game/start")
    public GameState startGame(@RequestParam FarmingMode mode) {
        GameState state = new GameState();
        state.setFarmingMode(mode);
        return state;
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
    public ResponseEntity<GameState> buyProductionCard(
            @RequestBody ProductionCardPurchaseRequest request) {

        GameState state = gameStateService.getState();
        if (state == null) {
            throw new IllegalStateException("Game not started");
        }

        if (state.getCurrentPhase() != GamePhase.INVEST) {
            throw new IllegalStateException(
                    "Production cards can only be bought during INVEST phase"
            );
        }

        purchaseService.buyProductionCard(state, request.getCardName());
        return ResponseEntity.ok(state);
    }


    // ===============================
    // MARKET
    // ===============================

    @GetMapping("/market")
    public List<ProductionCardDefinition> getMarket() {
        return productionMarket.getVisibleCards();
    }
}
