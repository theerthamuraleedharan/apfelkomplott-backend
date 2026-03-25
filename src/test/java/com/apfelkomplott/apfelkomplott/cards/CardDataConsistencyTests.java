package com.apfelkomplott.apfelkomplott.cards;

import com.apfelkomplott.apfelkomplott.repository.EventCardRepository;
import com.apfelkomplott.apfelkomplott.repository.ProductionCardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CardDataConsistencyTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void productionCardsLoadWithUniqueIdsAndKnownSpecialIds() throws IOException {
        ProductionCardRepository repository = new ProductionCardRepository(objectMapper);

        List<ProductionCardDef> cards = repository.all();
        Set<String> cardIds = cards.stream()
                .map(ProductionCardDef::getId)
                .collect(Collectors.toSet());

        assertFalse(cards.isEmpty(), "Production cards should not be empty");
        assertEquals(cards.size(), cardIds.size(), "Production card IDs must be unique");
        assertTrue(cardIds.contains(ProductionCardIds.WATER_MANAGEMENT));
        assertTrue(cardIds.contains(ProductionCardIds.WATER_MANAGEMENT_PRIVATE_WELL));
        assertTrue(cardIds.contains(ProductionCardIds.WATER_MANAGEMENT_ECO));
        assertTrue(cardIds.contains(ProductionCardIds.SHADE_NETS));
        assertEquals(ProductionCardIds.WATER_MANAGEMENT_IDS, ProductionCardIds.resolveGroup(ProductionCardIds.GROUP_WATER_MANAGEMENT));
        assertEquals(ProductionCardIds.SHADE_NET_IDS, ProductionCardIds.resolveGroup(ProductionCardIds.GROUP_SHADE_NETS));
    }

    @Test
    void productionCardsHaveValidReferencesAndExistingMediaAssets() throws IOException {
        ProductionCardRepository repository = new ProductionCardRepository(objectMapper);

        List<ProductionCardDef> cards = repository.all();
        Set<String> cardIds = cards.stream()
                .map(ProductionCardDef::getId)
                .collect(Collectors.toSet());

        for (ProductionCardDef card : cards) {
            assertNotNull(card.getId(), "Production card ID should be present");
            assertNotNull(card.getName(), "Production card name should be present");
            assertNotNull(card.getDeck(), "Production card deck should be present");

            List<String> requires = card.getRequires();
            if (requires != null) {
                for (String requiredId : requires) {
                    assertTrue(cardIds.contains(requiredId),
                            "Missing required production card reference: " + requiredId + " for " + card.getId());
                }
            }

            assertMediaSourcesExist(card.getMedia(), card.getId());
        }

        assertTrue(ProductionCardIds.containsWaterManagementCard(List.of("OTHER", ProductionCardIds.WATER_MANAGEMENT_ECO)));
        assertTrue(ProductionCardIds.containsShadeNetCard(List.of(ProductionCardIds.SHADE_NETS)));
        assertFalse(ProductionCardIds.containsWaterManagementCard(List.of("OTHER")));
        assertFalse(ProductionCardIds.containsShadeNetCard(null));
    }

    @Test
    void eventCardsLoadWithUniqueIdsAndExistingMediaAssets() throws IOException {
        EventCardRepository repository = new EventCardRepository(objectMapper);

        List<EventCardDef> cards = repository.all();
        Set<String> cardIds = cards.stream()
                .map(EventCardDef::getId)
                .collect(Collectors.toSet());

        assertFalse(cards.isEmpty(), "Event cards should not be empty");
        assertEquals(cards.size(), cardIds.size(), "Event card IDs must be unique");

        for (EventCardDef card : cards) {
            assertNotNull(card.getId(), "Event card ID should be present");
            assertNotNull(card.getName(), "Event card name should be present");
            assertNotNull(card.getDescription(), "Event card description should be present");
            assertNotNull(card.getEffects(), "Event card effects should be present");
            assertFalse(card.getEffects().isEmpty(), "Event card effects should not be empty for " + card.getId());
            assertMediaSourcesExist(card.getMedia(), card.getId());
        }
    }

    private void assertMediaSourcesExist(List<MediaItem> mediaItems, String ownerId) {
        if (mediaItems == null) {
            return;
        }

        for (MediaItem mediaItem : mediaItems) {
            if (mediaItem == null || mediaItem.getSrc() == null || mediaItem.getSrc().isBlank()) {
                continue;
            }

            String resourcePath = "static" + mediaItem.getSrc();
            assertTrue(new ClassPathResource(resourcePath).exists(),
                    "Missing media resource " + resourcePath + " for " + ownerId);
        }

        assertFalse(mediaItems.stream().filter(Objects::nonNull).map(MediaItem::getSrc).toList().contains(""),
                "Blank media src found for " + ownerId);
    }
}
