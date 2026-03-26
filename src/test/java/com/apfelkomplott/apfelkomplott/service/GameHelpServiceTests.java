package com.apfelkomplott.apfelkomplott.service;

import com.apfelkomplott.apfelkomplott.controller.dto.GameGuideDto;
import com.apfelkomplott.apfelkomplott.controller.dto.PhaseHelpDto;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameHelpServiceTests {

    private final GameHelpService service = new GameHelpService();

    @Test
    void buildGuideReturnsPlayerFriendlyOverviewAndAllPhases() {
        GameGuideDto guide = service.buildGuide();

        assertEquals("How To Play Apfelkomplott", guide.getTitle());
        assertNotNull(guide.getOverview());
        assertFalse(guide.getSetupSteps().isEmpty());
        assertFalse(guide.getBeginnerTips().isEmpty());
        assertEquals(GamePhase.values().length, guide.getPhases().size());
        assertTrue(guide.getPhases().stream().anyMatch(phase -> phase.getPhase() == GamePhase.DRAW_EVENT));
    }

    @Test
    void buildCurrentPhaseHelpReturnsWelcomeMessageWhenGameHasNotStarted() {
        PhaseHelpDto help = service.buildCurrentPhaseHelp(null);

        assertNull(help.getPhase());
        assertEquals("Welcome", help.getTitle());
        assertTrue(help.getWhatToDo().contains("Open the full guide"));
    }

    @Test
    void buildCurrentPhaseHelpReturnsSpecificTextForCurrentPhase() {
        GameState state = new GameState();
        state.setCurrentPhase(GamePhase.INVEST);

        PhaseHelpDto help = service.buildCurrentPhaseHelp(state);

        assertEquals(GamePhase.INVEST, help.getPhase());
        assertEquals("Choose An Investment", help.getTitle());
        assertTrue(help.getWhatToDo().contains("Pick upgrades carefully"));
    }
}
