package com.apfelkomplott.apfelkomplott.controller.dto;

import java.util.ArrayList;
import java.util.List;

public class GameGuideDto {

    private String title;
    private String overview;
    private String winCondition;
    private List<String> setupSteps = new ArrayList<>();
    private List<String> beginnerTips = new ArrayList<>();
    private List<PhaseHelpDto> phases = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getWinCondition() {
        return winCondition;
    }

    public void setWinCondition(String winCondition) {
        this.winCondition = winCondition;
    }

    public List<String> getSetupSteps() {
        return setupSteps;
    }

    public void setSetupSteps(List<String> setupSteps) {
        this.setupSteps = setupSteps;
    }

    public List<String> getBeginnerTips() {
        return beginnerTips;
    }

    public void setBeginnerTips(List<String> beginnerTips) {
        this.beginnerTips = beginnerTips;
    }

    public List<PhaseHelpDto> getPhases() {
        return phases;
    }

    public void setPhases(List<PhaseHelpDto> phases) {
        this.phases = phases;
    }
}
