package com.itmart.gharkharch.Utilities;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class Utility_UiAlerts {

    private final Context context;
    private static Utility_UiAlerts instance;

    private Utility_UiAlerts(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized Utility_UiAlerts getInstance(Context context) {
        if (instance == null) {
            instance = new Utility_UiAlerts(context);
        }
        return instance;
    }

    public static void generateSnackBar(View view, String msg) {
        new Handler(Looper.getMainLooper()).post(() -> Snackbar.make(view, msg, 700).show());
    }

    public void generateToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
