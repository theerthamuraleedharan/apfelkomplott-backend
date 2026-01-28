package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.GameState;
import org.springframework.stereotype.Service;

@Service
public class GameStateService {

    private GameState currentState;

    public GameState createNewGame(GameState state) {
        this.currentState = state;
        return currentState;
    }

    public GameState getState() {
        return currentState;
    }

    public GameState updateState(GameState state) {
        this.currentState = state;
        return currentState;
    }
}
