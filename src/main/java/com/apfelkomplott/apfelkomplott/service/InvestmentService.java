package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.controller.dto.InvestmentType;
import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

@Service
public class InvestmentService {

    private static final int MAX_TREES_PER_FIELD = 8;

    public void invest(GameState state, InvestmentType type) {

        if (state.isGameOver()) return;

        // 🚫 Rule 1: Only allowed in INVEST phase
        if (state.getCurrentPhase() != GamePhase.INVEST) {
            throw new IllegalStateException(
                    "Can only invest during INVEST phase"
            );
        }

        // 🚫 Rule 2: Round 1 restrictions (PDF rule)
        /*if (state.getCurrentRound() == 1 && isTreeOrLogistics(type)) {
            throw new IllegalStateException(
                    "Trees and logistics cannot be bought in Round 1"
            );
        }*/

        // ✅ Execute investment
        switch (type) {
            case BUY_SEEDLING -> buySeedling(state);
            case BUY_PRE_GROWN_TREE -> buyPreGrownTree(state);
            case BUY_CRATE -> buyCrate(state);
            case BUY_SALES_STAND -> buySalesStand(state);
        }
    }

    // ===== Actions =====
    private void buySeedling(GameState state) {
        ensureFieldHasCapacity(state);
        ensureEnoughMoney(state, 3, "Not enough money to buy a seedling.");

        Tree tree = new Tree();
        tree.setType(TreeType.SEEDLING);
        tree.setFieldPosition(1);

        state.getPlantation().getTrees().add(tree);
        state.setMoney(state.getMoney() - 3);
    }

    private void buyPreGrownTree(GameState state) {
        ensureFieldHasCapacity(state);
        ensureEnoughMoney(state, 4, "Not enough money to buy a pre-grown tree.");

        Tree tree = new Tree();
        tree.setType(TreeType.PRE_GROWN);
        tree.setFieldPosition(2);

        state.getPlantation().getTrees().add(tree);
        state.setMoney(state.getMoney() - 4);
    }

    private void buyCrate(GameState state) {
        ensureEnoughMoney(state, 3, "Not enough money to buy a crate.");

        state.getPlantation().getCrates().add(new Crate());
        state.setMoney(state.getMoney() - 3);
    }

    private void buySalesStand(GameState state) {
        ensureEnoughMoney(state, 3, "Not enough money to buy a sales stand.");

        state.getPlantation().getSalesStands().add(new SalesStand());
        state.setMoney(state.getMoney() - 3);
    }

    private void ensureFieldHasCapacity(GameState state) {
        int currentTreeCount = state.getPlantation().getTrees().size();
        if (currentTreeCount >= MAX_TREES_PER_FIELD) {
            throw new IllegalStateException("A field can contain at most 8 plants.");
        }
    }

    private void ensureEnoughMoney(GameState state, int requiredAmount, String message) {
        if (state.getMoney() < requiredAmount) {
            throw new IllegalStateException(message);
        }
    }
}
