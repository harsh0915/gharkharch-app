package com.itmart.gharkharch.Utilities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

public class Utility_DialogInflater {

    public interface DialogSetupCallback {
        void setupView(View dialog_layout);
    }

    public static Dialog inflateDialog(Context context, int layoutResId,DialogSetupCallback callback) {
        // Use AlertDialog.Builder for consistency and modern Android dialog practices
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate the custom layout for the dialog
        View dialog_layout = LayoutInflater.from(context).inflate(layoutResId, null);
        builder.setView(dialog_layout);

        if (callback != null) {
            callback.setupView(dialog_layout);
        }

        // Create the dialog
        Dialog dialog = builder.create();

        // Set transparent background for the dialog window
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return dialog;
    }

}