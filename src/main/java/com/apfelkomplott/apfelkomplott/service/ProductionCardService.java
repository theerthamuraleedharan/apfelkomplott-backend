package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.Enum.CardDeck;
import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.Enum.PlantationSize;
import com.apfelkomplott.apfelkomplott.cards.ActiveProductionCard;
import com.apfelkomplott.apfelkomplott.cards.EffectDef;
import com.apfelkomplott.apfelkomplott.cards.ProductionCardDef;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.ScoreResult;
import com.apfelkomplott.apfelkomplott.entity.ScoreTrack;
import com.apfelkomplott.apfelkomplott.repository.ProductionCardRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductionCardService {
  // Special long-term card that converts the farm from conventional to organic.
  private static final String PRODUCTION_METHOD_CARD_ID = "LT_PRODUCTION_METHOD";

  private final ProductionCardRepository repo;
  private final Random random = new Random();

  public ProductionCardService(ProductionCardRepository repo) {
    this.repo = repo;
  }

  public void initDeckAndMarket(GameState state) {
    if (!state.getProductionDrawPile().isEmpty()) return;

    List<ProductionCardDef> all = new ArrayList<>(repo.all());
    Collections.shuffle(all, random);

    state.getProductionDrawPile().clear();
    for (ProductionCardDef c : all) state.getProductionDrawPile().add(c.getId());

    refillMarketToFive(state);
    refreshMarketView(state);
  }

  public ScoreResult buyCard(GameState state, String cardId) {

    if (!state.getMarketCardIds().contains(cardId)) {
        throw new IllegalStateException("Card is not in market row.");
    }

    ProductionCardDef card = repo.getById(cardId);

    List<String> requires = (card.getRequires() == null)
            ? Collections.emptyList()
            : card.getRequires();

    for (String req : requires) {
        boolean hasReq = state.getActiveLongTerm().stream()
                .anyMatch(a -> req.equals(a.getCardId()));
        if (!hasReq) throw new IllegalStateException("Missing prerequisite: " + req);
    }

    int cost = resolveCost(card, state.getFarmingMode());
    if (state.getMoney() < cost) {
        throw new IllegalStateException("Not enough money.");
    }

    state.setMoney(state.getMoney() - cost);

    int idx = state.getMarketCardIds().indexOf(cardId);
    if (idx >= 0) {
        state.getMarketCardIds().set(idx, null);
    }

    PlantationSize plantationSizeAtPurchase = null;
    if (usesPlantationSize(card)) {
        plantationSizeAtPurchase = resolvePlantationSize(state);
    }

    ScoreResult result = new ScoreResult(0, 0, 0);

    if (card.getDeck() == CardDeck.LONG_TERM) {
        // Long-term cards are stored and scored in later rounds instead of on purchase.
        state.getActiveLongTerm().add(
                new ActiveProductionCard(
                        cardId,
                        state.getCurrentRound(),
                        plantationSizeAtPurchase
                )
        );

        // Buying the production-method card immediately updates the farm mode so
        // future scoring uses the organic branch of its effects.
        if (PRODUCTION_METHOD_CARD_ID.equals(cardId)) {
            state.setFarmingMode(FarmingMode.ORGANIC);
        }
    } else {
        // Short-term cards still apply immediately, but buying them should not
        // open a scoring popup. Apply the score change silently.
        applyEffectsForYear(state, card, 1, plantationSizeAtPurchase, null);

        state.getProductionDiscardPile().add(cardId);
        state.getShortTermUsedThisRound().add(cardId);
    }

    if (state.getScoreTrack().isGameOver()) {
        state.setGameOver(true);
    }

    return result;
}

  private int resolveCost(ProductionCardDef card, FarmingMode mode) {
    if (card.getCost() == null) return 0;
    return card.getCost().resolve(mode);
  }

  private boolean usesPlantationSize(ProductionCardDef card) {
    return card.getEffectsByPlantationSize() != null && !card.getEffectsByPlantationSize().isEmpty();
  }

  private PlantationSize resolvePlantationSize(GameState state) {

    int treeCount = getTreeCount(state);

    if (treeCount <= 16) {
      return PlantationSize.SMALL;
    }

    if (treeCount <= 32) {
      return PlantationSize.MEDIUM;
    }

    return PlantationSize.LARGE;
  }

  private int getTreeCount(GameState state) {
    return state.getPlantation().getTrees().size();
  }

  private List<EffectDef> resolveEffects(
        ProductionCardDef card,
        FarmingMode mode,
        PlantationSize plantationSize
) {
    if (card.getEffectsByPlantationSize() != null && plantationSize != null) {
        List<EffectDef> bySize = card.getEffectsByPlantationSize().get(plantationSize);
        if (bySize != null) return bySize;
    }

    if (card.getEffectsByMode() != null && mode != null) {
        List<EffectDef> byMode = card.getEffectsByMode().get(mode);
        if (byMode != null) return byMode;
    }

    return card.getEffects();
}

  private void applyEffectsForYear(
        GameState state,
        ProductionCardDef card,
        int year,
        PlantationSize plantationSize,
        ScoreResult result
) {
    List<EffectDef> effects = resolveEffects(card, state.getFarmingMode(), plantationSize);
    if (effects == null) return;

    ScoreTrack score = state.getScoreTrack();

    for (EffectDef e : effects) {
        if (e.appliesInYear(year)) {

            score.setEconomy(score.getEconomy() + e.getEconomy());
            score.setEnvironment(score.getEnvironment() + e.getEnvironment());
            score.setHealth(score.getHealth() + e.getHealth());

           if (result != null) {
              result.setEconomyChange(result.getEconomyChange() + e.getEconomy());
              result.setEnvironmentChange(result.getEnvironmentChange() + e.getEnvironment());
              result.setHealthChange(result.getHealthChange() + e.getHealth());

              if (e.getEconomy() != 0) {
                  result.addReason(formatReason(card, plantationSize, year, e.getEconomy(), "Economy"));
              }
              if (e.getEnvironment() != 0) {
                  result.addReason(formatReason(card, plantationSize, year, e.getEnvironment(), "Environment"));
              }
              if (e.getHealth() != 0) {
                  result.addReason(formatReason(card, plantationSize, year, e.getHealth(), "Health"));
              }
           }
        }
    }
}

  public ScoreResult applyLongTermCardScoring(GameState state) {
    pruneExpiredActiveCards(state);
    int round = state.getCurrentRound();
    ScoreResult result = new ScoreResult(0, 0, 0);

    for (ActiveProductionCard active : new ArrayList<>(state.getActiveLongTerm())) {

      ProductionCardDef card = repo.getById(active.getCardId());

      int year = round - active.getPurchasedRound() + 1;

      if (year <= 0) continue;

      applyEffectsForYear(
              state,
              card,
              year,
              active.getPlantationSizeAtPurchase(),
              result
      );
    }

    pruneExpiredActiveCards(state);

    if (state.getScoreTrack().isGameOver()) {
        state.setGameOver(true);
    }
    

    return result;
}

  public void refreshMarketView(GameState state) {
    state.setMarket(
            state.getMarketCardIds().stream()
                    .map(id -> id == null ? null : repo.getById(id))
                    .toList()
    );
  }

  public List<ProductionCardDef> getMarketCards(GameState state) {
    return state.getMarketCardIds().stream()
            .map(id -> id == null ? null : repo.getById(id))
            .toList();
  }

  public List<ProductionCardDef> getActiveProductionCards(GameState state) {
    pruneExpiredActiveCards(state);
    return state.getActiveLongTerm().stream()
            .map(active -> repo.getById(active.getCardId()))
            .toList();
  }

  private void pruneExpiredActiveCards(GameState state) {
    int round = state.getCurrentRound();
    state.getActiveLongTerm().forEach(active -> normalizeActiveCardProgression(active, round));
    state.getActiveLongTerm().removeIf(active -> !isCardStillActive(state, active, round));
  }

  private boolean isCardStillActive(GameState state, ActiveProductionCard active, int round) {
    if (active.getRemainingYears() != null) {
      return !active.getRemainingYears().isEmpty();
    }

    if (active.getCurrentYear() != null || active.getFinalApplicableYear() != null) {
      int currentYear = active.getCurrentYear() != null
              ? active.getCurrentYear()
              : round - active.getPurchasedRound() + 1;
      int finalYear = active.getFinalApplicableYear() != null
              ? active.getFinalApplicableYear()
              : resolveMaxApplicableYear(state, active);
      return currentYear <= finalYear;
    }

    return round - active.getPurchasedRound() + 1 <= resolveMaxApplicableYear(state, active);
  }

  private void normalizeActiveCardProgression(ActiveProductionCard active, int round) {
    int derivedYear = round - active.getPurchasedRound() + 1;

    if (active.getRemainingYears() != null) {
      active.getRemainingYears().removeIf(year -> year < derivedYear);
    }

    if (active.getCurrentYear() != null) {
      active.setCurrentYear(Math.max(active.getCurrentYear(), derivedYear));
    }
  }

  private int resolveMaxApplicableYear(GameState state, ActiveProductionCard active) {
    ProductionCardDef card = repo.getById(active.getCardId());
    List<EffectDef> effects = resolveEffects(
            card,
            state.getFarmingMode(),
            active.getPlantationSizeAtPurchase()
    );

    if (effects == null) {
      return 0;
    }

    return effects.stream()
            .map(EffectDef::getYears)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .mapToInt(Integer::intValue)
            .max()
            .orElse(0);
  }

  public void refillMarketToFive(GameState state) {
    boolean freshMarket = state.getMarketCardIds().stream().allMatch(Objects::isNull);

    while (state.getMarketCardIds().size() < 5) {
      state.getMarketCardIds().add(null);
    }

    int shortTermCount = countMarketCardsByDeck(state, CardDeck.SHORT_TERM);
    int longTermCount = countMarketCardsByDeck(state, CardDeck.LONG_TERM);

    while (shortTermCount < 3 && state.getMarketCardIds().contains(null)) {
      String nextId = drawNextCardForDeck(state, CardDeck.SHORT_TERM);
      if (nextId == null) break;
      putCardIntoNextMarketHole(state, nextId);
      shortTermCount++;
    }

    while (longTermCount < 2 && state.getMarketCardIds().contains(null)) {
      String nextId = drawNextCardForDeck(state, CardDeck.LONG_TERM);
      if (nextId == null) break;
      putCardIntoNextMarketHole(state, nextId);
      longTermCount++;
    }

    while (state.getMarketCardIds().contains(null)) {
      String nextId = drawNextAvailableCard(state);
      if (nextId == null) break;
      putCardIntoNextMarketHole(state, nextId);
    }

    if (freshMarket) {
      Collections.shuffle(state.getMarketCardIds(), random);
    }
  }

  private int countMarketCardsByDeck(GameState state, CardDeck deck) {
    return (int) state.getMarketCardIds().stream()
            .filter(Objects::nonNull)
            .map(repo::getById)
            .filter(card -> card.getDeck() == deck)
            .count();
  }

  private void putCardIntoNextMarketHole(GameState state, String cardId) {
    int holeIndex = state.getMarketCardIds().indexOf(null);
    if (holeIndex >= 0) {
      state.getMarketCardIds().set(holeIndex, cardId);
    }
  }

  private String drawNextCardForDeck(GameState state, CardDeck deck) {
    while (true) {
      int index = findNextCardIndex(state, deck);
      if (index >= 0) {
        return state.getProductionDrawPile().remove(index);
      }

      discardUnavailableDrawPileCards(state);
      if (!reloadDrawPile(state)) {
        return null;
      }
    }
  }

  private String drawNextAvailableCard(GameState state) {
    while (true) {
      for (int i = 0; i < state.getProductionDrawPile().size(); i++) {
        String cardId = state.getProductionDrawPile().get(i);
        if (!isUnavailableForMarket(state, cardId)) {
          return state.getProductionDrawPile().remove(i);
        }
      }

      discardUnavailableDrawPileCards(state);
      if (!reloadDrawPile(state)) {
        return null;
      }
    }
  }

  private int findNextCardIndex(GameState state, CardDeck deck) {
    for (int i = 0; i < state.getProductionDrawPile().size(); i++) {
      String cardId = state.getProductionDrawPile().get(i);
      ProductionCardDef card = repo.getById(cardId);

      if (card.getDeck() == deck && !isUnavailableForMarket(state, cardId)) {
        return i;
      }
    }

    return -1;
  }

  private boolean isUnavailableForMarket(GameState state, String cardId) {
    boolean alreadyInMarket = state.getMarketCardIds().stream()
            .filter(Objects::nonNull)
            .anyMatch(id -> id.equals(cardId));

    boolean alreadyActive = state.getActiveLongTerm().stream()
            .anyMatch(a -> cardId.equals(a.getCardId()));

    return alreadyInMarket || alreadyActive;
  }

  private boolean reloadDrawPile(GameState state) {
    if (state.getProductionDiscardPile().isEmpty()) {
      return false;
    }

    Collections.shuffle(state.getProductionDiscardPile(), random);
    state.getProductionDrawPile().addAll(state.getProductionDiscardPile());
    state.getProductionDiscardPile().clear();
    return true;
  }

  private void discardUnavailableDrawPileCards(GameState state) {
    Iterator<String> iterator = state.getProductionDrawPile().iterator();
    while (iterator.hasNext()) {
      String cardId = iterator.next();
      if (isUnavailableForMarket(state, cardId)) {
        state.getProductionDiscardPile().add(cardId);
        iterator.remove();
      }
    }
  }

  private String formatReason(
        ProductionCardDef card,
        PlantationSize size,
        int year,
        int value,
        String stat
    ) {
        String sign = value > 0 ? "+" : "";
        String sizeText = "";

        if (size != null) {
            sizeText = " - " + size.name().toLowerCase() + " plantation";
        }

        return sign + value + " " + stat +
                " (" + card.getName() + sizeText + " - Year " + year + ")";
    }
}
