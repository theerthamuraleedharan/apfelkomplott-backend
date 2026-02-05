package com.apfelkomplott.apfelkomplott.market;

import com.apfelkomplott.apfelkomplott.entity.EventCardDefinition;
import com.apfelkomplott.apfelkomplott.entity.EventType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

@Component
public class EventCardDeck {

    private final List<EventCardDefinition> allCards = new ArrayList<>();
    private final Stack<EventCardDefinition> deck = new Stack<>();

    public EventCardDeck() {

        allCards.add(new EventCardDefinition(
                "Unwetter",
                "Hagel und Überschwemmung verursachen Ernteausfall",
                EventType.UNWETTER
        ));

        allCards.add(new EventCardDefinition(
                "Vorschriften",
                "Neue Umweltauflagen treten in Kraft",
                EventType.VORSCHRIFTEN
        ));

        allCards.add(new EventCardDefinition(
                "Schädlingsausbruch",
                "Schädlinge gefährden die Ernte",
                EventType.SCHAEDLINGSAUSBRUCH
        ));

        allCards.add(new EventCardDefinition(
                "Marktpreise steigen",
                "Apfelpreise steigen um +2",
                EventType.MARKTPREISE_STEIGEN
        ));

        allCards.add(new EventCardDefinition(
                "Marktpreise sinken",
                "Apfelpreise sinken um -1",
                EventType.MARKTPREISE_SINKEN
        ));

        reshuffle();
    }

    private void reshuffle() {
        deck.clear();
        List<EventCardDefinition> shuffled = new ArrayList<>(allCards);
        Collections.shuffle(shuffled);
        deck.addAll(shuffled);
    }

    public EventCardDefinition draw() {
        if (deck.isEmpty()) {
            reshuffle(); // 🔥 IMPORTANT FIX
        }
        return deck.pop();
    }
}
