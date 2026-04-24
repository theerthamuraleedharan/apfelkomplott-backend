package com.apfelkomplott.apfelkomplott.controller;

import com.apfelkomplott.apfelkomplott.engine.RoundEngine;
import com.apfelkomplott.apfelkomplott.entity.GamePhase;
import com.apfelkomplott.apfelkomplott.entity.GameState;
import com.apfelkomplott.apfelkomplott.service.EventService;
import com.apfelkomplott.apfelkomplott.service.GameHelpService;
import com.apfelkomplott.apfelkomplott.service.GameInitializer;
import com.apfelkomplott.apfelkomplott.service.GameStateService;
import com.apfelkomplott.apfelkomplott.service.InvestmentService;
import com.apfelkomplott.apfelkomplott.service.ProductionCardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
class GameControllerErrorHandlingTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameInitializer gameInitializer;

    @MockitoBean
    private RoundEngine roundEngine;

    @MockitoBean
    private InvestmentService investmentService;

    @MockitoBean
    private GameStateService gameStateService;

    @MockitoBean
    private ProductionCardService productionCardService;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private GameHelpService gameHelpService;

    @Test
    void selectEventReturnsBadRequestForServiceLevelIllegalArgument() throws Exception {
        GameState state = stateInPhase(GamePhase.DRAW_EVENT);
        when(gameStateService.getState()).thenReturn(state);
        when(eventService.selectEvent(any(GameState.class), eq(5)))
                .thenThrow(new IllegalArgumentException("Invalid event option index: 5"));

        mockMvc.perform(post("/game/event/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"optionIndex":5}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid event option index: 5"));
    }

    @Test
    void investReturnsBadRequestWhenInvestmentTypeIsMissing() throws Exception {
        GameState state = stateInPhase(GamePhase.INVEST);
        when(gameStateService.getState()).thenReturn(state);

        mockMvc.perform(post("/game/invest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("investmentType is required."));
    }

    @Test
    void buyProductionReturnsBadRequestWhenCardIdIsBlank() throws Exception {
        GameState state = stateInPhase(GamePhase.INVEST);
        when(gameStateService.getState()).thenReturn(state);

        mockMvc.perform(post("/game/invest/production")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cardId":" "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("cardId is required."));
    }

    private GameState stateInPhase(GamePhase phase) {
        GameState state = new GameState();
        state.setCurrentPhase(phase);
        return state;
    }
}
