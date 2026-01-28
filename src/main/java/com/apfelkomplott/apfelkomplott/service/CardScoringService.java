package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.ProductionCard;
import org.springframework.stereotype.Service;

import com.apfelkomplott.apfelkomplott.entity.GameState;

@Service
public class CardScoringService {

    public void applyCardScoring(GameState state) {

        if (state.isGameOver()) return;

        for (ProductionCard card : state.getActiveProductionCards()) {
            card.apply(state);
        }

        if (state.getScoreTrack().isGameOver()) {
            state.setGameOver(true);
        }
    }
}
