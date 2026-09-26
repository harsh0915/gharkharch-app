package com.itmart.gharkharch.DataModels;

public enum GenericItemType {

    Category(1),
    Sub_Category(2),
    Expenses(3);

    private final int value;

    GenericItemType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

