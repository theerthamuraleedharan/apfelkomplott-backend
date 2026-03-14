package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.Enum.CardDeck;
import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.cards.ActiveProductionCard;
import com.apfelkomplott.apfelkomplott.cards.EffectDef;
import com.apfelkomplott.apfelkomplott.cards.ProductionCardDef;
import com.apfelkomplott.apfelkomplott.controller.dto.ActiveLongTermCardView;
import com.apfelkomplott.apfelkomplott.controller.dto.BuyProductionRequest;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.Plantation;
import com.apfelkomplott.apfelkomplott.entity.PlantationLayoutRules;
import com.apfelkomplott.apfelkomplott.entity.PlantationSize;
import com.apfelkomplott.apfelkomplott.entity.ScoreResult;
import com.apfelkomplott.apfelkomplott.entity.ScoreTrack;
import com.apfelkomplott.apfelkomplott.repository.ProductionCardRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductionCardService {

  private static final String RULE_PLANTATION_LAYOUT = "PLANTATION_LAYOUT";
  private final ProductionCardRepository repo;
  private final Random random = new Random();

  public ProductionCardService(ProductionCardRepository repo) {
    this.repo = repo;
  }

  /** Call once at new game start */
  public void initDeckAndMarket(GameState state) {
    if (!state.getProductionDrawPile().isEmpty()) return;

    List<ProductionCardDef> all = new ArrayList<>(repo.all());
    Collections.shuffle(all, random);

    state.getProductionDrawPile().clear();
    for (ProductionCardDef c : all) state.getProductionDrawPile().add(c.getId());

    refillMarketToFive(state);
    refreshMarketView(state);
  }

  /** Buy: remove immediately, apply Year-1 immediately, store if long-term */
  public void buyCard(GameState state, BuyProductionRequest request) {
    String cardId = request.getCardId();

    if (!state.getMarketCardIds().contains(cardId)) {
      throw new IllegalStateException("Card is not in market row.");
    }

    ProductionCardDef card = repo.getById(cardId);

    // prerequisites
    List<String> requires = (card.getRequires() == null) ? Collections.emptyList() : card.getRequires();
    for (String req : requires) {
      boolean hasReq = state.getActiveLongTerm().stream()
              .anyMatch(a -> req.equals(a.getCardId()));
      if (!hasReq) throw new IllegalStateException("Missing prerequisite: " + req);
    }

    // cost
    int cost = resolveCost(card, state.getFarmingMode());
    if (state.getMoney() < cost) throw new IllegalStateException("Not enough money.");

    // pay
    state.setMoney(state.getMoney() - cost);

    // ✅ DO NOT REMOVE. Keep the same slot empty until Step 3.
    int idx = state.getMarketCardIds().indexOf(cardId);
    if (idx >= 0) {
      state.getMarketCardIds().set(idx, null);
    }

    // apply Year 1 now
    applyEffectsForYear(state, card, 1);

    // store
    if (card.getDeck() == CardDeck.LONG_TERM) {
      List<Integer> plantationLayout = resolvePlantationLayout(state, card, request.getPlantationLayout());
      state.getActiveLongTerm().add(new ActiveProductionCard(cardId, state.getCurrentRound(), plantationLayout));
    } else {
      state.getProductionDiscardPile().add(cardId);
      state.getShortTermUsedThisRound().add(cardId);
    }

    if (state.getScoreTrack().isGameOver()) {
      state.setGameOver(true);
    }
  }
  private int resolveCost(ProductionCardDef card, FarmingMode mode) {
    if (card.getCost() == null) return 0;
    return card.getCost().resolve(mode);
  }

  private List<EffectDef> resolveEffects(ProductionCardDef card, FarmingMode mode) {
    if (card.getEffectsByMode() != null && mode != null) {
      List<EffectDef> byMode = card.getEffectsByMode().get(mode);
      if (byMode != null) return byMode;
    }
    return card.getEffects();
  }

  private void applyEffectsForYear(GameState state, ProductionCardDef card, int year) {
    List<EffectDef> effects = resolveEffects(card, state.getFarmingMode());
    if (effects == null) return;

    ScoreTrack score = state.getScoreTrack();

    for (EffectDef e : effects) {
      if (e.appliesInYear(year)) {
        score.setEconomy(score.getEconomy() + e.getEconomy());
        score.setEnvironment(score.getEnvironment() + e.getEnvironment());
        score.setHealth(score.getHealth() + e.getHealth());
      }
    }
  }

  private void applyScoreDelta(ScoreTrack score, ScoreResult result, int economy, int environment, int health, String reason) {
    score.setEconomy(score.getEconomy() + economy);
    score.setEnvironment(score.getEnvironment() + environment);
    score.setHealth(score.getHealth() + health);

    result.setEconomyChange(result.getEconomyChange() + economy);
    result.setEnvironmentChange(result.getEnvironmentChange() + environment);
    result.setHealthChange(result.getHealthChange() + health);
    result.addReason(reason);
  }

  private List<Integer> resolvePlantationLayout(GameState state, ProductionCardDef card, List<Integer> plantationLayout) {
    if (RULE_PLANTATION_LAYOUT.equals(card.getCustomRule())) {
      return snapshotPlantationLayout(state.getPlantation());
    }

    return validatePlantationLayout(card, plantationLayout);
  }

  private List<Integer> snapshotPlantationLayout(Plantation plantation) {
    int treeCount = plantation.getTrees().size();
    if (treeCount <= 0) {
      throw new IllegalStateException("This card requires at least one tree in the plantation.");
    }

    return List.of(treeCount);
  }

  private List<Integer> validatePlantationLayout(ProductionCardDef card, List<Integer> plantationLayout) {
    if (!RULE_PLANTATION_LAYOUT.equals(card.getCustomRule())) {
      return plantationLayout == null ? Collections.emptyList() : List.copyOf(plantationLayout);
    }

    if (plantationLayout == null || plantationLayout.isEmpty()) {
      throw new IllegalStateException("This card requires a plantation layout.");
    }

    return List.copyOf(plantationLayout);
  }

  private void applyCustomRuleForYear(GameState state, ProductionCardDef card, ActiveProductionCard active, int year, ScoreResult result) {
    if (!RULE_PLANTATION_LAYOUT.equals(card.getCustomRule())) {
      return;
    }

    if (year < 2 || year > 3) {
      return;
    }

    ScoreTrack score = state.getScoreTrack();
    for (Integer areaSize : active.getPlantationLayout()) {
      PlantationSize plantationSize = PlantationLayoutRules.classify(areaSize);
      switch (plantationSize) {
        case LARGE -> {
          applyScoreDelta(score, result, 2, -1, 0,
                  card.getName() + ": " + areaSize + " trees counted as LARGE in year " + year + " (Economy +2, Environment -1)");
        }
        case MEDIUM -> applyScoreDelta(score, result, 1, 0, 0,
                card.getName() + ": " + areaSize + " trees counted as MEDIUM in year " + year + " (Economy +1)");
        case SMALL -> {
          applyScoreDelta(score, result, -1, 1, 0,
                  card.getName() + ": " + areaSize + " trees counted as SMALL in year " + year + " (Economy -1, Environment +1)");
        }
      }
    }
  }

 public ScoreResult applyLongTermCardScoring(GameState state) {
    int round = state.getCurrentRound();
    ScoreTrack score = state.getScoreTrack();
    ScoreResult result = new ScoreResult(0, 0, 0);

    Iterator<ActiveProductionCard> it = state.getActiveLongTerm().iterator();

    while (it.hasNext()) {
        ActiveProductionCard active = it.next();
        ProductionCardDef card = repo.getById(active.getCardId());

        int year = round - active.getPurchasedRound() + 1;

        // Year 1 already applied during buyCard()
        if (year == 2 || year == 3) {
            List<EffectDef> effects = resolveEffects(card, state.getFarmingMode());

            if (effects != null) {
                for (EffectDef e : effects) {
                    if (e.appliesInYear(year)) {
                        applyScoreDelta(
                                score,
                                result,
                                e.getEconomy(),
                                e.getEnvironment(),
                                e.getHealth(),
                                card.getName() + ": year " + year + " effect applied (Economy "
                                        + formatSigned(e.getEconomy()) + ", Environment "
                                        + formatSigned(e.getEnvironment()) + ", Health "
                                        + formatSigned(e.getHealth()) + ")"
                        );
                    }
                }
            }

            applyCustomRuleForYear(state, card, active, year, result);
        }

        // remove after Year 3
        if (year > 3) {
            it.remove();
            state.getProductionDiscardPile().add(card.getId());
        }
    }

    if (score.isGameOver()) {
        state.setGameOver(true);
    }

    return result;
 }

  private String formatSigned(int value) {
    return value >= 0 ? "+" + value : Integer.toString(value);
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

  public void refillMarketToFive(GameState state) {
    // Ensure the market always has 5 slots (may contain null holes)
    while (state.getMarketCardIds().size() < 5) {
      state.getMarketCardIds().add(null);
    }

    // Fill holes one-by-one
    while (state.getMarketCardIds().contains(null)) {

      if (state.getProductionDrawPile().isEmpty()) {
        if (state.getProductionDiscardPile().isEmpty()) break;
        Collections.shuffle(state.getProductionDiscardPile(), random);
        state.getProductionDrawPile().addAll(state.getProductionDiscardPile());
        state.getProductionDiscardPile().clear();
      }

      if (state.getProductionDrawPile().isEmpty()) break;

      String nextId = state.getProductionDrawPile().remove(0);

      // avoid duplicates in row or already-active long-term
      boolean alreadyInMarket = state.getMarketCardIds().stream()
              .filter(Objects::nonNull)
              .anyMatch(id -> id.equals(nextId));

      boolean alreadyActive = state.getActiveLongTerm().stream()
              .anyMatch(a -> nextId.equals(a.getCardId()));

      if (alreadyInMarket || alreadyActive) {
        state.getProductionDiscardPile().add(nextId);
        continue;
      }

      int holeIndex = state.getMarketCardIds().indexOf(null);
      if (holeIndex >= 0) {
        state.getMarketCardIds().set(holeIndex, nextId);
      } else {
        break;
      }
    }
  }


/**
     * Frontend-ready view for active long-term cards:
     * currentYear = 1..3
     */
    public List<ActiveLongTermCardView> getActiveLongTermView(GameState state) {
        int round = state.getCurrentRound();

        return state.getActiveLongTerm().stream()
                .map(active -> {
                    ProductionCardDef card = repo.getById(active.getCardId());
                    int year = round - active.getPurchasedRound() + 1;

                    return new ActiveLongTermCardView(
                            card.getId(),
                            card.getName(),
                            year,
                            Math.max(0, 3 - year),
                            card
                    );
                })
                .filter(view -> view.getCurrentYear() >= 1 && view.getCurrentYear() <= 3)
                .collect(Collectors.toList());
    }
}
