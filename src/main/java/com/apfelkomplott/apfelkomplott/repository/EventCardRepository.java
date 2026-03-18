package com.apfelkomplott.apfelkomplott.repository;

import com.apfelkomplott.apfelkomplott.cards.EventCardDef;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class EventCardRepository {

    private final Map<String, EventCardDef> byId;
    private final List<EventCardDef> all;

    public EventCardRepository(ObjectMapper mapper) throws IOException {
        var is = new ClassPathResource("static/cards/event_cards.json").getInputStream();
        this.all = mapper.readValue(is, new TypeReference<List<EventCardDef>>() {});
        this.byId = all.stream().collect(Collectors.toMap(EventCardDef::getId, card -> card));
    }

    public List<EventCardDef> all() {
        return all;
    }

    public EventCardDef getById(String id) {
        EventCardDef card = byId.get(id);
        if (card == null) {
            throw new IllegalArgumentException("Event card not found: " + id);
        }
        return card;
    }
}
