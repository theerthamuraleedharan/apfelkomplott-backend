package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.Enum.CardDeck;
import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.cards.ActiveProductionCard;
import com.apfelkomplott.apfelkomplott.cards.EffectDef;
import com.apfelkomplott.apfelkomplott.cards.ProductionCardDef;
import com.apfelkomplott.apfelkomplott.entity.GameState;
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
  public void buyCard(GameState state, String cardId) {

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
      state.getActiveLongTerm().add(new ActiveProductionCard(cardId, state.getCurrentRound()));
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

  public void applyLongTermCardScoring(GameState state) {
    int round = state.getCurrentRound();

    for (ActiveProductionCard active : state.getActiveLongTerm()) {
      ProductionCardDef card = repo.getById(active.getCardId());

      // buy round = Year 1 (already applied immediately in buyCard)
      int year = round - active.getPurchasedRound() + 1;
      if (year <= 1) continue;

      List<EffectDef> effects = resolveEffects(card, state.getFarmingMode());
      if (effects == null) continue;

      for (EffectDef e : effects) {
        if (e.appliesInYear(year)) {
          state.getScoreTrack().setEconomy(
                  state.getScoreTrack().getEconomy() + e.getEconomy()
          );
          state.getScoreTrack().setEnvironment(
                  state.getScoreTrack().getEnvironment() + e.getEnvironment()
          );
          state.getScoreTrack().setHealth(
                  state.getScoreTrack().getHealth() + e.getHealth()
          );
        }
      }
    }

    if (state.getScoreTrack().isGameOver()) {
      state.setGameOver(true);
    }
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


}
