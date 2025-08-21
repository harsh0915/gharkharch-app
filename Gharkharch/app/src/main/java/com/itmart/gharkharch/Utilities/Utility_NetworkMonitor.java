package com.itmart.gharkharch.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.itmart.gharkharch.R;

public class Utility_NetworkMonitor {

    private static ConnectivityManager.NetworkCallback networkCallback;

    public static void registerNetworkCallback(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return;

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
            }

            @Override
            public void onLost(@NonNull Network network) {
            }
        };

        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    public static void unregisterNetworkCallback(Context context) {
        if (networkCallback == null) return;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
}

