package com.itmart.gharkharch.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.itmart.gharkharch.Activities.LoginActivity;
import com.itmart.gharkharch.R;

public class Utility_UiAlerts {

    public static final String ERROR = "error";
    public static final String SUCCESS = "success";
    public static final String WARNING = "warning";
    public static final String DEFAULT = "default";
    private Dialog dialog;

    public static void generateSnackBar(View view, String msg, String type) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Snackbar snackbar = Snackbar.make(view, " ", 800);

            int iconRes;
            int bgColor;
            int textColor;

            switch (type) {
                case ERROR:
                    textColor = Color.WHITE;
                    iconRes = R.drawable.error_icon;
                    bgColor = ContextCompat.getColor(view.getContext(), R.color.error_red);
                    break;

                case WARNING:
                    textColor = Color.BLACK;
                    iconRes = R.drawable.warning_icon;
                    bgColor = ContextCompat.getColor(view.getContext(), R.color.warning_yellow);
                    break;

                case SUCCESS:
                    textColor = Color.WHITE;
                    iconRes = R.drawable.confirmation_icon;
                    bgColor = ContextCompat.getColor(view.getContext(), R.color.confirmation_green);
                    break;

                default:
                    textColor = Color.BLACK;
                    iconRes = R.drawable.default_alert_icon;
                    bgColor = ContextCompat.getColor(view.getContext(), R.color.white);
                    break;
            }

            TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(textColor);

            Drawable icon = ContextCompat.getDrawable(view.getContext(), iconRes);
            if (icon != null) {
                icon.setBounds(0, 0, textView.getLineHeight(), textView.getLineHeight());
                ImageSpan imageSpan = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);

                SpannableString spannable = new SpannableString("  " + msg);
                spannable.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                textView.setText(spannable, TextView.BufferType.SPANNABLE);
            } else {
                textView.setText(msg);
            }
            snackbar.setBackgroundTint(bgColor);
            snackbar.show();
        });
    }

    public void showLoadingDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_generic, null);
        builder.setView(layout);
        dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismissLoadingDialog() {
        dialog.dismiss();
    }

    public static void generateToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
