package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.Enum.FarmingMode;
import com.apfelkomplott.apfelkomplott.cards.ActiveProductionCard;
import com.apfelkomplott.apfelkomplott.cards.EffectDef;
import com.apfelkomplott.apfelkomplott.cards.ProductionCardDef;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.ScoreTrack;
import com.apfelkomplott.apfelkomplott.repository.ProductionCardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardScoringService {

    private final ProductionCardRepository repo;

    public CardScoringService(ProductionCardRepository repo) {
        this.repo = repo;
    }

    public void applyCardScoring(GameState state) {

        int round = state.getCurrentRound();
        ScoreTrack score = state.getScoreTrack();

        for (ActiveProductionCard active : state.getActiveLongTerm()) {

            ProductionCardDef card = repo.getById(active.getCardId());

            // year since purchase:
            // bought round -> year 1 already applied immediately
            // next round -> year 2, etc.
            int year = round - active.getPurchasedRound() + 1;
            if (year <= 1) continue;

            List<EffectDef> effects = resolveEffects(card, state.getFarmingMode());
            if (effects == null) continue;

            for (EffectDef e : effects) {
                if (e.appliesInYear(year)) {
                    score.setEconomy(score.getEconomy() + e.getEconomy());
                    score.setEnvironment(score.getEnvironment() + e.getEnvironment());
                    score.setHealth(score.getHealth() + e.getHealth());
                }
            }
        }

        if (score.isGameOver()) state.setGameOver(true);
    }

    private List<EffectDef> resolveEffects(ProductionCardDef card, FarmingMode mode) {
        if (card.getEffectsByMode() != null && mode != null) {
            List<EffectDef> byMode = card.getEffectsByMode().get(mode);
            if (byMode != null) return byMode;
        }
        return card.getEffects();
    }
}