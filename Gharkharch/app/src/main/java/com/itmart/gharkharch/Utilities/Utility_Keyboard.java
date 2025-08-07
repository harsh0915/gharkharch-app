package com.itmart.gharkharch.Utilities;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utility_Keyboard {

    private final Context context;
    private static Utility_Keyboard instance;

    private Utility_Keyboard(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized Utility_Keyboard getInstance(Context context) {
        if (instance == null) {
            instance = new Utility_Keyboard(context);
        }
        return instance;
    }

    public void close_keyboard(View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
