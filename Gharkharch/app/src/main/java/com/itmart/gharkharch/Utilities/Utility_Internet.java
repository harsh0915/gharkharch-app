package com.itmart.gharkharch.Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;


public class Utility_Internet {

    public interface CheckInternetAvailabilityCallback {
        void checkInternet(boolean val);
    }

    public static void checkInternetAvailability(Context context, CheckInternetAvailabilityCallback callback) {

        final boolean[] isCallbackUnregistered = {false};

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            callback.checkInternet(false);
            return;
        }

        NetworkRequest request = new NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            private void unregisterSafe() {
                if (!isCallbackUnregistered[0]) {
                    try {
                        cm.unregisterNetworkCallback(this);
                    } catch (Exception e) {
                        Log.e("UtilityInternetDebugger", "Something went wrong while unregistering network callback! Error : " + e);
                    }
                    isCallbackUnregistered[0] = true;
                }
            }

            @Override
            public void onAvailable(@NonNull Network network) {
                callback.checkInternet(true);
                unregisterSafe();
            }

            @Override
            public void onLost(@NonNull Network network) {
                callback.checkInternet(false);
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, NetworkCapabilities networkCapabilities) {

                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    callback.checkInternet(true);
                    unregisterSafe();
                } else {
                    callback.checkInternet(false);
                }
            }
        };
        cm.registerNetworkCallback(request, networkCallback);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isCallbackUnregistered[0]) {
                try {
                    cm.unregisterNetworkCallback(networkCallback);
                } catch (Exception e) {
                    // Already unregistered, ignore
                }
                isCallbackUnregistered[0] = true;
                callback.checkInternet(false);
            }
        }, 1000);
    }

}
