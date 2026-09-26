package com.itmart.gharkharch.DataModels;

public enum PaymentMode {
    CASH(1),
    UPI(2),
    CARD(3);

    private final int value;

    PaymentMode(int value) {
        this.value = value;
    }

    //use to store int in db
    public int getValue() {
        return value;
    }

    //reverse look up func to get enum value from int stored in db
    public static PaymentMode fromValue(int value) {
        for (PaymentMode mode : PaymentMode.values()) {
            if (mode.getValue() == value) return mode;
        }
        return null;
    }
}