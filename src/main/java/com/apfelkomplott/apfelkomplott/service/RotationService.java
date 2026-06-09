package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.entity.Tree;
import org.springframework.stereotype.Service;

import java.util.Iterator;

/**
 * Advances trees through the field positions at the end of a round.
 */
@Service
public class RotationService {

    /**
     * Moves every tree one field forward and removes trees that leave field six.
     *
     * @param state current game state
     */
    public void rotate(GameState state) {

        Iterator<Tree> iterator = state.getPlantation().getTrees().iterator();

        while (iterator.hasNext()) {

            Tree tree = iterator.next();
            int newPosition = tree.getFieldPosition() + 1;

            if (newPosition > 6) {
                iterator.remove();
            } else {
                tree.setFieldPosition(newPosition);
            }
        }
    }
}

