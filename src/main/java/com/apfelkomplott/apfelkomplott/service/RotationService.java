package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.Tree;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class RotationService {

    /**
     * STEP 7 – Rotate Plantation
     * Moves all trees one field forward.
     * Trees leaving field 6 are removed.
     */
    public void rotate(GameState state) {

        Iterator<Tree> iterator = state.getPlantation().getTrees().iterator();

        while (iterator.hasNext()) {
            Tree tree = iterator.next();
            int nextField = tree.getFieldPosition() + 1;

            if (nextField > 6) {
                // Tree leaves plantation
                iterator.remove();
            } else {
                tree.setFieldPosition(nextField);
            }
        }
    }
}
