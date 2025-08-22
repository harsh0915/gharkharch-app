package com.itmart.gharkharch.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

public class Utility_NetworkMonitor {

    public static void registerNetworkCallback(Context context, ConnectivityManager.NetworkCallback networkCallback) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return;

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    public static void unregisterNetworkCallback(Context context, ConnectivityManager.NetworkCallback networkCallback) {
        if (networkCallback == null) return;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }
}

