package com.apfelkomplott.apfelkomplott.entity;

import java.util.UUID;

public class Tree {

    private UUID id;
    private int fieldPosition; // 1–6
    private TreeType type;     // SEEDLING, PRE_GROWN

    public Tree() {
        this.id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public int getFieldPosition() {
        return fieldPosition;
    }

    public void setFieldPosition(int fieldPosition) {
        this.fieldPosition = fieldPosition;
    }

    public TreeType getType() {
        return type;
    }

    public void setType(TreeType type) {
        this.type = type;
    }

    public boolean isMature() {
        return fieldPosition >= 3 && fieldPosition <= 6;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
