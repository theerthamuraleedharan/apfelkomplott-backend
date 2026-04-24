package com.apfelkomplott.apfelkomplott.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class EventSelectionRequest {

    @NotNull(message = "optionIndex is required.")
    @Min(value = 0, message = "optionIndex must be zero or greater.")
    private Integer optionIndex;

    public Integer getOptionIndex() {
        return optionIndex;
    }

    public void setOptionIndex(Integer optionIndex) {
        this.optionIndex = optionIndex;
    }
}
