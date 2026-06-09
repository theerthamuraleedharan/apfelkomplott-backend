package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

/**
 * Creates starting game states for normal gameplay and demonstration flows.
 */
@Service
public class GameInitializer {

    private final GameStateService gameStateService;

    public GameInitializer(GameStateService gameStateService) {
        this.gameStateService = gameStateService;
    }

    /**
     * Creates and stores an empty new game state.
     *
     * @return initialized game state
     */
    public GameState createNewGame() {
        GameState state = new GameState();
        return gameStateService.createNewGame(state);
    }

    /**
     * Creates a demo game with one starting tree for quick UI testing.
     *
     * @return demo game state
     */
    public GameState createDemoGame() {
        GameState state = new GameState();

        Tree tree = new Tree();
        tree.setType(TreeType.SEEDLING);
        tree.setFieldPosition(1);

        state.getPlantation().getTrees().add(tree);

        return state;
    }

}

