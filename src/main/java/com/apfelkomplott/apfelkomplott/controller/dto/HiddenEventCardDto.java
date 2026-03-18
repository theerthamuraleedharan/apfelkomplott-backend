package com.apfelkomplott.apfelkomplott.controller.dto;

public class HiddenEventCardDto {

    private int optionIndex;

    public HiddenEventCardDto() {
    }

    public HiddenEventCardDto(int optionIndex) {
        this.optionIndex = optionIndex;
    }

    public int getOptionIndex() {
        return optionIndex;
    }

    public void setOptionIndex(int optionIndex) {
        this.optionIndex = optionIndex;
    }
}
