package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.GameState;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stores active game states for browser sessions.
 */
@Service
public class GameStateService {

    private static final String DEFAULT_GAME_ID = "default";

    private final Map<String, GameState> statesByGameId = new ConcurrentHashMap<>();

    /**
     * Replaces the default game state used by legacy API endpoints.
     *
     * @param state new game state
     * @return stored game state
     */
    public GameState createNewGame(GameState state) {
        return createNewGame(DEFAULT_GAME_ID, state);
    }

    /**
     * Creates a new game with a generated id for an independent browser session.
     *
     * @param state new game state
     * @return stored game state with its generated id assigned
     */
    public GameState createNewGameWithGeneratedId(GameState state) {
        return createNewGame(UUID.randomUUID().toString(), state);
    }

    /**
     * Stores a game under a specific id.
     *
     * @param gameId session or game identifier
     * @param state new game state
     * @return stored game state
     */
    public GameState createNewGame(String gameId, GameState state) {
        if (state == null) {
            statesByGameId.remove(gameId);
            return null;
        }

        state.setGameId(gameId);
        statesByGameId.put(gameId, state);
        return state;
    }

    /**
     * Returns the default game state used by legacy API endpoints.
     *
     * @return active game state, or {@code null} if no game has been started
     */
    public GameState getState() {
        return getState(DEFAULT_GAME_ID);
    }

    /**
     * Returns the game state for the supplied id.
     *
     * @param gameId session or game identifier
     * @return active game state, or {@code null} if no game has been started
     */
    public GameState getState(String gameId) {
        return statesByGameId.get(gameId);
    }

    /**
     * Stores updates to the default game state.
     *
     * @param state updated game state
     * @return stored game state
     */
    public GameState updateState(GameState state) {
        return updateState(DEFAULT_GAME_ID, state);
    }

    /**
     * Stores updates to a game state for the supplied id.
     *
     * @param gameId session or game identifier
     * @param state updated game state
     * @return stored game state
     */
    public GameState updateState(String gameId, GameState state) {
        if (state == null) {
            statesByGameId.remove(gameId);
            return null;
        }

        state.setGameId(gameId);
        statesByGameId.put(gameId, state);
        return state;
    }
}
