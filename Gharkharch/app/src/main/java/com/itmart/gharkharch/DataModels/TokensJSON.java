package com.itmart.gharkharch.DataModels;

public class TokensJSON {
    private final String refresh_token;
    private final String access_token;
    private final String message;

    public TokensJSON(String refresh_token, String access_token, String message) {
        this.refresh_token = refresh_token;
        this.access_token = access_token;
        this.message = message;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getMessage() {
        return message;
    }
}
