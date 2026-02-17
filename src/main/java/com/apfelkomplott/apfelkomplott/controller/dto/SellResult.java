package com.apfelkomplott.apfelkomplott.controller.dto;

public class SellResult {

    private int applesSold;
    private int moneyEarned;

    public SellResult(int applesSold, int moneyEarned) {
        this.applesSold = applesSold;
        this.moneyEarned = moneyEarned;
    }

    public int getApplesSold() {
        return applesSold;
    }

    public int getMoneyEarned() {
        return moneyEarned;
    }
}
