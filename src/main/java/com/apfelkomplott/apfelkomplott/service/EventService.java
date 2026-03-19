package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.Enum.PlantationSize;
import com.apfelkomplott.apfelkomplott.cards.EventCardDef;
import com.apfelkomplott.apfelkomplott.cards.EventEffectDef;
import com.apfelkomplott.apfelkomplott.cards.EventEffectType;
import com.apfelkomplott.apfelkomplott.cards.ProductionCardIds;
import com.apfelkomplott.apfelkomplott.controller.dto.HiddenEventCardDto;
import com.apfelkomplott.apfelkomplott.entity.EventResolution;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.repository.EventCardRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class EventService {

    private static final int EVENT_CHOICES_PER_DRAW = 2;

    private final EventCardRepository repository;
    private final Random random = new Random();

    public EventService(EventCardRepository repository) {
        this.repository = repository;
    }

    public void initDeck(GameState state) {
        if (!state.getEventDrawPile().isEmpty()) {
            return;
        }

        List<EventCardDef> cards = new ArrayList<>(repository.all());
        Collections.shuffle(cards, random);

        state.getEventDrawPile().clear();
        state.getEventDiscardPile().clear();
        state.getPendingEventOptions().clear();

        for (EventCardDef card : cards) {
            state.getEventDrawPile().add(card.getId());
        }
    }

    public void prepareDrawIfNeeded(GameState state) {
        requireDrawEventPhase(state);

        if (!state.getPendingEventOptions().isEmpty()) {
            return;
        }

        initDeck(state);
        state.setLastEventResult(null);
        state.getRoundEventImpact().clear();

        // Offer only unseen cards from the current cycle. Once the draw pile is empty,
        // the discard pile is reshuffled to start a fresh cycle in a later round.
        if (state.getEventDrawPile().isEmpty()) {
            reloadDiscardIntoDrawPile(state);
        }

        int cardsToDraw = Math.min(EVENT_CHOICES_PER_DRAW, state.getEventDrawPile().size());
        for (int i = 0; i < cardsToDraw; i++) {
            String nextCardId = state.getEventDrawPile().remove(0);
            if (nextCardId == null) {
                break;
            }
            state.getPendingEventOptions().add(nextCardId);
        }
    }

    public List<HiddenEventCardDto> getHiddenOptions(GameState state) {
        prepareDrawIfNeeded(state);

        List<HiddenEventCardDto> options = new ArrayList<>();
        for (int i = 0; i < state.getPendingEventOptions().size(); i++) {
            options.add(new HiddenEventCardDto(i));
        }
        return options;
    }

    public EventResolution selectEvent(GameState state, int optionIndex) {
        requireDrawEventPhase(state);
        prepareDrawIfNeeded(state);

        if (optionIndex < 0 || optionIndex >= state.getPendingEventOptions().size()) {
            throw new IllegalArgumentException("Invalid event option index: " + optionIndex);
        }

        String selectedCardId = state.getPendingEventOptions().get(optionIndex);
        EventCardDef selectedCard = repository.getById(selectedCardId);
        EventResolution resolution = applyEffects(state, selectedCard);

        List<String> unselectedCardIds = new ArrayList<>(state.getPendingEventOptions());
        unselectedCardIds.remove(optionIndex);

        state.getEventDiscardPile().add(selectedCardId);
        returnUnselectedCardsToDrawPile(state, unselectedCardIds);
        state.getPendingEventOptions().clear();
        state.setLastEventResult(resolution);
        state.setCurrentPhase(GamePhase.REFILL_CARDS);

        return resolution;
    }

    public PlantationSize resolvePlantationSize(GameState state) {
        int treeCount = state.getPlantation().getTrees().size();

        if (treeCount <= 16) {
            return PlantationSize.SMALL;
        }

        if (treeCount <= 32) {
            return PlantationSize.MEDIUM;
        }

        return PlantationSize.LARGE;
    }

    public int calculateHarvestLoss(GameState state) {
        if (!state.getRoundEventImpact().hasHarvestLoss()) {
            return 0;
        }

        PlantationSize size = resolvePlantationSize(state);
        Integer numerator = state.getRoundEventImpact().getHarvestLossByPlantationSize().get(size);
        Integer divisor = state.getRoundEventImpact().getHarvestLossDivisor();

        if (numerator == null || divisor == null || divisor <= 0) {
            return 0;
        }

        long matureTrees = state.getPlantation().getTrees().stream()
                .filter(tree -> tree.isMature())
                .count();

        return (int) Math.ceil((double) matureTrees * numerator / divisor);
    }

    private EventResolution applyEffects(GameState state, EventCardDef card) {
        EventResolution resolution = new EventResolution();
        resolution.setCardId(card.getId());
        resolution.setCardName(card.getName());
        resolution.setDescription(card.getDescription());
        resolution.setMedia(card.getMedia());

        PlantationSize plantationSize = resolvePlantationSize(state);
        resolution.setPlantationSize(plantationSize);

        for (EventEffectDef effect : card.getEffects()) {
            if (effect.getType() == EventEffectType.MONEY_DELTA) {
                applyMoneyDelta(state, resolution, effect);
            } else if (effect.getType() == EventEffectType.MONEY_DELTA_BY_FARMING_MODE) {
                applyMoneyDeltaByFarmingMode(state, resolution, effect);
            } else if (effect.getType() == EventEffectType.HARVEST_LOSS_BY_PLANTATION_SIZE) {
                applyHarvestLossEffect(state, resolution, plantationSize, effect);
            } else if (effect.getType() == EventEffectType.APPLE_PRICE_MODIFIER_DELTA) {
                applyApplePriceModifierDelta(state, resolution, effect);
            } else if (effect.getType() == EventEffectType.PRODUCTION_CARD_COST_DELTA) {
                applyProductionCardCostDelta(state, resolution, effect);
            }
        }

        return resolution;
    }

    private void applyMoneyDelta(GameState state, EventResolution resolution, EventEffectDef effect) {
        int moneyDelta = effect.getAmount() == null ? 0 : effect.getAmount();
        state.setMoney(state.getMoney() + moneyDelta);
        resolution.setMoneyChange(resolution.getMoneyChange() + moneyDelta);
        resolution.addEffect(formatMoneyEffect(moneyDelta));
    }

    private void applyMoneyDeltaByFarmingMode(GameState state, EventResolution resolution, EventEffectDef effect) {
        FarmingMode farmingMode = state.getFarmingMode();
        Integer moneyDelta = effect.getAmountByFarmingMode().get(farmingMode);
        if (moneyDelta == null) {
            return;
        }

        state.setMoney(state.getMoney() + moneyDelta);
        resolution.setMoneyChange(resolution.getMoneyChange() + moneyDelta);
        resolution.addEffect(formatMoneyEffectByFarmingMode(moneyDelta, farmingMode));
    }

    private void applyHarvestLossEffect(
            GameState state,
            EventResolution resolution,
            PlantationSize plantationSize,
            EventEffectDef effect
    ) {
        state.getRoundEventImpact().clear();
        state.getRoundEventImpact().setHarvestLossDivisor(effect.getDivisor());
        state.getRoundEventImpact().getHarvestLossByPlantationSize().putAll(effect.getAmountByPlantationSize());

        Integer numerator = effect.getAmountByPlantationSize().get(plantationSize);
        if (numerator == null || effect.getDivisor() == null || effect.getDivisor() <= 0) {
            return;
        }

        int harvestLoss = calculateHarvestLoss(state);
        resolution.setExpectedHarvestLoss(harvestLoss);
        resolution.addEffect(
                harvestLoss + " harvest lost for "
                        + plantationSize.name().toLowerCase()
                        + " plantation (" + numerator + "/" + effect.getDivisor() + ")"
        );
    }

    private void applyApplePriceModifierDelta(GameState state, EventResolution resolution, EventEffectDef effect) {
        int modifierDelta = effect.getAmount() == null ? 0 : effect.getAmount();

        // This event changes the plantation-wide sale bonus, not the value of a
        // single apple in the current moment. Because of that, the modifier is
        // stored on the plantation and reused by SellService every time apples
        // are sold in later rounds.
        //
        // The effect is cumulative:
        // - first Premium card  -> modifier becomes +1
        // - second Premium card -> modifier becomes +2
        // - and so on
        //
        // SellService then calculates:
        // final price per apple = base price (1) + current plantation modifier
        state.getPlantation().setApplePriceModifier(
                state.getPlantation().getApplePriceModifier() + modifierDelta
        );

        // Expose both the delta from this card and the resulting total modifier
        // so the frontend can clearly explain what changed to the player.
        resolution.setSaleBonusPerAppleChange(resolution.getSaleBonusPerAppleChange() + modifierDelta);
        resolution.setResultingSaleBonusPerApple(state.getPlantation().getApplePriceModifier());
        resolution.addEffect(formatApplePriceModifierEffect(modifierDelta));
    }

    private void applyProductionCardCostDelta(GameState state, EventResolution resolution, EventEffectDef effect) {
        int modifierDelta = effect.getAmount() == null ? 0 : effect.getAmount();
        List<String> targetCardIds = resolveTargetCardIds(effect);

        if (modifierDelta == 0 || targetCardIds.isEmpty()) {
            return;
        }

        for (String cardId : targetCardIds) {
            int currentModifier = state.getProductionCardCostModifiers().getOrDefault(cardId, 0);
            state.getProductionCardCostModifiers().put(cardId, currentModifier + modifierDelta);
        }

        resolution.setProductionCardCostChange(resolution.getProductionCardCostChange() + modifierDelta);
        resolution.addEffect(formatProductionCardCostEffect(targetCardIds, modifierDelta));
    }

    private void reloadDiscardIntoDrawPile(GameState state) {
        if (state.getEventDiscardPile().isEmpty()) {
            return;
        }

        Collections.shuffle(state.getEventDiscardPile(), random);
        state.getEventDrawPile().addAll(state.getEventDiscardPile());
        state.getEventDiscardPile().clear();
    }

    private void returnUnselectedCardsToDrawPile(GameState state, List<String> unselectedCardIds) {
        if (unselectedCardIds.isEmpty()) {
            return;
        }

        Collections.shuffle(unselectedCardIds, random);
        state.getEventDrawPile().addAll(unselectedCardIds);
    }

    private void requireDrawEventPhase(GameState state) {
        if (state.getCurrentPhase() != GamePhase.DRAW_EVENT) {
            throw new IllegalStateException("Event cards can only be handled during DRAW_EVENT phase.");
        }
    }

    private String formatMoneyEffect(int moneyDelta) {
        return moneyDelta >= 0 ? "+" + moneyDelta + " money" : moneyDelta + " money";
    }

    private String formatApplePriceModifierEffect(int modifierDelta) {
        String sign = modifierDelta >= 0 ? "+" : "";
        return sign + modifierDelta + " money for each sold apple from now on";
    }

    private String formatMoneyEffectByFarmingMode(int moneyDelta, FarmingMode farmingMode) {
        return formatMoneyEffect(moneyDelta) + " (" + farmingMode.name().toLowerCase() + " farming)";
    }

    private String formatProductionCardCostEffect(List<String> targetCardIds, int modifierDelta) {
        String sign = modifierDelta >= 0 ? "+" : "";

        if (ProductionCardIds.containsWaterManagementCard(targetCardIds)) {
            return "Water Management costs " + sign + modifierDelta + " money from now on";
        }

        if (ProductionCardIds.containsShadeNetCard(targetCardIds)) {
            return "Shade Nets cost " + sign + modifierDelta + " money from now on";
        }

        if (targetCardIds.size() == 1) {
            return "Production card " + targetCardIds.get(0) + " costs " + sign + modifierDelta + " money from now on";
        }

        return "Selected production cards cost " + sign + modifierDelta + " money from now on";
    }

    private List<String> resolveTargetCardIds(EventEffectDef effect) {
        Set<String> resolvedIds = new LinkedHashSet<>();

        if (effect.getTargetCardIds() != null) {
            resolvedIds.addAll(effect.getTargetCardIds());
        }

        if (effect.getTargetCardGroup() != null && !effect.getTargetCardGroup().isBlank()) {
            resolvedIds.addAll(ProductionCardIds.resolveGroup(effect.getTargetCardGroup()));
        }

        return new ArrayList<>(resolvedIds);
    }
}
