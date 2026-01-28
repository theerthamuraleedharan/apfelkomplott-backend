package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

@Service
public class GameInitializer {

    private final GameStateService gameStateService;

    public GameInitializer(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    public GameState createNewGame() {
        GameState state = new GameState();
        return gameStateService.createNewGame(state);
    }

    /**
     * Demo game with 1 starting tree (for UI testing)
     */
    public GameState createDemoGame() {
        GameState state = new GameState();

        Tree tree = new Tree();
        tree.setType(TreeType.SEEDLING);
        tree.setFieldPosition(1);

        Crate crate = new Crate();

        state.getPlantation().getTrees().add(tree);
        state.getPlantation().getCrates().add(crate);

        return state;
    }

}

