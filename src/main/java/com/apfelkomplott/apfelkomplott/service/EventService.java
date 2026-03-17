package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.Enum.EventEffectType;
import com.apfelkomplott.apfelkomplott.Enum.EventTiming;
import com.apfelkomplott.apfelkomplott.entity.Apple;
import com.apfelkomplott.apfelkomplott.entity.EventCard;
import com.apfelkomplott.apfelkomplott.entity.EventCardDefinition;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


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

    private void applyImmediateEffect(EventCard card, GameState state) {

        if (card.getEffectType() == EventEffectType.HARVEST_REDUCTION) {

            int treeCount = state.getPlantation().getTrees().size();
            List<Apple> apples = state.getPlantation().getApples();

            int totalApples = apples.size();
            int reduction = 0;

            if (treeCount >= 6) {
                reduction = totalApples / 3;
            } else if (treeCount >= 3) {
                reduction = totalApples / 3;
            } else {
                reduction = (totalApples * 2) / 3;
            }

            for (int i = 0; i < reduction && !apples.isEmpty(); i++) {
                apples.remove(0);
            }
        }
    }


    public List<EventCard> createEventDeck() {

        List<EventCard> deck = new ArrayList<>();

        deck.add(new EventCard(
                "Severe Weather",
                "Hail and flooding cause crop failures.",
                EventTiming.IMMEDIATE,
                EventEffectType.HARVEST_REDUCTION
        ));

        deck.add(new EventCard(
                "Regulations",
                "New environmental regulations.",
                EventTiming.DELAYED,
                EventEffectType.CONVERT_TO_ORGANIC
        ));

        return deck;
    }

    public EventCard drawEvent(GameState state) {

        if (state.getEventDeck().isEmpty()) {
            return null;
        }

        EventCard card = state.getEventDeck().remove(0);

        if (card.getTiming() == EventTiming.IMMEDIATE) {
            applyImmediateEffect(card, state);
        } else {
            state.getActiveEvents().add(card);
        }

        return card;
    }



}
