package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.EventCardDefinition;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import org.springframework.stereotype.Service;

import static com.apfelkomplott.apfelkomplott.entity.EventType.*;

@Service
public class EventService {

    public void applyEvent(GameState state, EventCardDefinition card) {

        switch (card.getType()) {

            case UNWETTER -> applyStorm(state);

            case VORSCHRIFTEN -> {
                state.getScoreTrack().setEnvironment(
                    state.getScoreTrack().getEnvironment() + 1
                );
            }

            case SCHAEDLINGSAUSBRUCH -> {
                state.setMoney(state.getMoney() - 1);
            }

            case MARKTPREISE_STEIGEN -> {
                state.getPlantation().setApplePriceModifier(+2);
            }

            case MARKTPREISE_SINKEN -> {
                state.getPlantation().setApplePriceModifier(-1);
            }
        }
    }

    private void applyStorm(GameState state) {
        // Example: remove some apples based on plantation size
        state.getPlantation().getApples().clear();
    }
}
