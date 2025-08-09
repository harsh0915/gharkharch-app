package com.itmart.gharkharch.Utilities;

import okhttp3.OkHttpClient;

public class Utility_OkHTTPClient {

    private Utility_OkHTTPClient() {
    }

    private static OkHttpClient client;

    public static OkHttpClient getHttpClient() {
        if (client == null) {
            client = new OkHttpClient().newBuilder().build();
        }
        return client;
    }
}
