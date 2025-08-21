package com.itmart.gharkharch.Utilities;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

public class Utility_UserPreferences {

    private final SharedPreferences UserPreferences;

    public Utility_UserPreferences(Context context) {
        this.UserPreferences = context.getSharedPreferences("tokens", MODE_PRIVATE);
    }

    public void saveTokens(String refresh_token, String access_token) {
        SharedPreferences.Editor editor = UserPreferences.edit();
        editor.putString("refresh_token", refresh_token);
        editor.putString("access_token", access_token);
        editor.apply();
    }

    public void saveUsername(String username) {
        SharedPreferences.Editor editor = UserPreferences.edit();
        editor.putString("username", username);
        editor.apply();
    }

    public String getRefreshToken() {
        return UserPreferences.getString("refresh_token", null);
    }

    public String getAccessToken() {
        return UserPreferences.getString("access_token", null);
    }

    public String getUsername() {
        return UserPreferences.getString("username", null);
    }
}
