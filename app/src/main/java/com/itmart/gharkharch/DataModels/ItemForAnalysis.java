package com.itmart.gharkharch.DataModels;

public class ItemForAnalysis {

    private final String name;
    private final int amount;

    public ItemForAnalysis(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }
}
