package com.apfelkomplott.apfelkomplott.controller.dto;

import com.apfelkomplott.apfelkomplott.entity.GamePhase;

public class PhaseHelpDto {

    private GamePhase phase;
    private String title;
    private String goal;
    private String whatToDo;
    private String whyItMatters;

    public PhaseHelpDto() {
    }

    public PhaseHelpDto(GamePhase phase, String title, String goal, String whatToDo, String whyItMatters) {
        this.phase = phase;
        this.title = title;
        this.goal = goal;
        this.whatToDo = whatToDo;
        this.whyItMatters = whyItMatters;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getWhatToDo() {
        return whatToDo;
    }

    public void setWhatToDo(String whatToDo) {
        this.whatToDo = whatToDo;
    }

    public String getWhyItMatters() {
        return whyItMatters;
    }

    public void setWhyItMatters(String whyItMatters) {
        this.whyItMatters = whyItMatters;
    }
}
