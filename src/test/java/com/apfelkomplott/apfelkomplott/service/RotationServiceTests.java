package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.Tree;
import com.apfelkomplott.apfelkomplott.entity.TreeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RotationServiceTests {

    private final RotationService service = new RotationService();

    @Test
    void rotateMovesTreesForwardAndRemovesTreesPastFieldSix() {
        GameState state = new GameState();
        addTree(state, 1);
        addTree(state, 6);

        service.rotate(state);

        assertEquals(1, state.getPlantation().getTrees().size());
        assertEquals(2, state.getPlantation().getTrees().get(0).getFieldPosition());
    }

    private void addTree(GameState state, int fieldPosition) {
        Tree tree = new Tree();
        tree.setType(TreeType.SEEDLING);
        tree.setFieldPosition(fieldPosition);
        state.getPlantation().getTrees().add(tree);
    }
}
