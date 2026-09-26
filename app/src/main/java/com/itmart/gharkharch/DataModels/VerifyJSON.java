package com.itmart.gharkharch.DataModels;

public class VerifyJSON {

    private final boolean access_granted;
    private final String message;

    public VerifyJSON(Boolean access_granted, String message) {
        this.access_granted = access_granted;
        this.message = message;
    }

    public boolean isAccess_granted() {
        return access_granted;
    }

    public String getMessage() {
        return message;
    }
}
