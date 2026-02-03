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

    private final Stack<EventCardDefinition> deck = new Stack<>();

    public EventCardDeck() {

        List<EventCardDefinition> cards = new ArrayList<>();

        cards.add(new EventCardDefinition(
                "Unwetter",
                "Hagel und Überschwemmung verursachen Ernteausfall",
                EventType.UNWETTER
        ));

        cards.add(new EventCardDefinition(
                "Vorschriften",
                "Neue Umweltauflagen treten in Kraft",
                EventType.VORSCHRIFTEN
        ));

        cards.add(new EventCardDefinition(
                "Schädlingsausbruch",
                "Schädlinge gefährden die Ernte",
                EventType.SCHAEDLINGSAUSBRUCH
        ));

        cards.add(new EventCardDefinition(
                "Marktpreise steigen",
                "Apfelpreise steigen um +2",
                EventType.MARKTPREISE_STEIGEN
        ));

        cards.add(new EventCardDefinition(
                "Marktpreise sinken",
                "Apfelpreise sinken um -1",
                EventType.MARKTPREISE_SINKEN
        ));

        Collections.shuffle(cards);
        deck.addAll(cards);
    }

    public EventCardDefinition draw() {
        if (deck.isEmpty()) {
            throw new IllegalStateException("No event cards left");
        }
        return deck.pop();
    }
}

