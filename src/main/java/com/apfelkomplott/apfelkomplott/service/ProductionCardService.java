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
        // store only, do NOT show explanation now
        state.getActiveLongTerm().add(
                new ActiveProductionCard(
                        cardId,
                        state.getCurrentRound(),
                        plantationSizeAtPurchase
                )
        );
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

  private List<EffectDef> resolveEffects(ProductionCardDef card, FarmingMode mode, PlantationSize plantationSize) {

    // 1️⃣ plantation size cards
    if (card.getEffectsByPlantationSize() != null && plantationSize != null) {

      List<EffectDef> bySize =
              card.getEffectsByPlantationSize().get(plantationSize);

      if (bySize != null) {
        return bySize;
      }
    }

    // 2️⃣ farming mode cards
    if (card.getEffectsByMode() != null && mode != null) {

      List<EffectDef> byMode =
              card.getEffectsByMode().get(mode);

      if (byMode != null) {
        return byMode;
      }
    }

    // 3️⃣ default cards
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

    int round = state.getCurrentRound();
    ScoreResult result = new ScoreResult(0, 0, 0);

    for (ActiveProductionCard active : state.getActiveLongTerm()) {

      ProductionCardDef card = repo.getById(active.getCardId());

      int year = round - active.getPurchasedRound() + 1;

      if (year <= 1) continue;

      System.out.println("Card: " + card.getName()
        + ", purchasedRound=" + active.getPurchasedRound()
        + ", currentRound=" + round
        + ", year=" + year
        + ", plantationSizeAtPurchase=" + active.getPlantationSizeAtPurchase());

System.out.println("effectsByPlantationSize = " + card.getEffectsByPlantationSize());
System.out.println("default effects = " + card.getEffects());


      applyEffectsForYear(
              state,
              card,
              year,
              active.getPlantationSizeAtPurchase(),
              result
      );
      System.out.println("Card: " + card.getName() + ", purchasedRound=" 
    + active.getPurchasedRound() + ", currentRound=" + round + ", year=" + year);
    }

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

  public void refillMarketToFive(GameState state) {
    while (state.getMarketCardIds().size() < 5) {
      state.getMarketCardIds().add(null);
    }

    while (state.getMarketCardIds().contains(null)) {

      if (state.getProductionDrawPile().isEmpty()) {
        if (state.getProductionDiscardPile().isEmpty()) break;
        Collections.shuffle(state.getProductionDiscardPile(), random);
        state.getProductionDrawPile().addAll(state.getProductionDiscardPile());
        state.getProductionDiscardPile().clear();
      }

      if (state.getProductionDrawPile().isEmpty()) break;

      String nextId = state.getProductionDrawPile().remove(0);

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
