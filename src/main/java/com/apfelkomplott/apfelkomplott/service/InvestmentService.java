package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.controller.dto.InvestmentType;
import com.apfelkomplott.apfelkomplott.entity.*;
import org.springframework.stereotype.Service;

@Service
public class InvestmentService {

    public void invest(GameState state, InvestmentType type) {

        if (state.isGameOver()) return;

        // 🚫 Rule 1: Only allowed in INVEST phase
        if (state.getCurrentPhase() != GamePhase.INVEST) {
            throw new IllegalStateException(
                    "Can only invest during INVEST phase"
            );
        }

        // 🚫 Rule 2: Round 1 restrictions (PDF rule)
        if (state.getCurrentRound() == 1 && isTreeOrLogistics(type)) {
            throw new IllegalStateException(
                    "Trees and logistics cannot be bought in Round 1"
            );
        }

        // ✅ Execute investment
        switch (type) {
            case BUY_SEEDLING -> buySeedling(state);
            case BUY_PRE_GROWN_TREE -> buyPreGrownTree(state);
            case BUY_CRATE -> buyCrate(state);
            case BUY_SALES_STAND -> buySalesStand(state);
        }
    }

    // ===== Helper =====
    private boolean isTreeOrLogistics(InvestmentType type) {
        return type == InvestmentType.BUY_SEEDLING
                || type == InvestmentType.BUY_PRE_GROWN_TREE
                || type == InvestmentType.BUY_CRATE
                || type == InvestmentType.BUY_SALES_STAND;
    }

    // ===== Actions =====
    private void buySeedling(GameState state) {
        if (state.getMoney() < 3) return;

        Tree tree = new Tree();
        tree.setType(TreeType.SEEDLING);
        tree.setFieldPosition(1);

        state.getPlantation().getTrees().add(tree);
        state.setMoney(state.getMoney() - 3);
    }

    private void buyPreGrownTree(GameState state) {
        if (state.getMoney() < 4) return;

        Tree tree = new Tree();
        tree.setType(TreeType.PRE_GROWN);
        tree.setFieldPosition(2);

        state.getPlantation().getTrees().add(tree);
        state.setMoney(state.getMoney() - 4);
    }

    private void buyCrate(GameState state) {
        if (state.getMoney() < 3) return;

        state.getPlantation().getCrates().add(new Crate());
        state.setMoney(state.getMoney() - 3);
    }

    private void buySalesStand(GameState state) {
        if (state.getMoney() < 3) return;

        state.getPlantation().getSalesStands().add(new SalesStand());
        state.setMoney(state.getMoney() - 3);
    }
}
