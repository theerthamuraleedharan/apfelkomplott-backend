package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.GameState;
import org.springframework.stereotype.Service;

/**
 * Stores the single active game state used by the current web-game session.
 */
@Service
public class GameStateService {

    private GameState currentState;

    /**
     * Replaces any previous game with a new game state.
     *
     * @param state new game state
     * @return stored game state
     */
    public GameState createNewGame(GameState state) {
        this.currentState = state;
        return currentState;
    }

    /**
     * Returns the current game state.
     *
     * @return active game state, or {@code null} if no game has been started
     */
    public GameState getState() {
        return currentState;
    }

    /**
     * Stores updates to the active game state.
     *
     * @param state updated game state
     * @return stored game state
     */
    public GameState updateState(GameState state) {
        this.currentState = state;
        return currentState;
    }
}
