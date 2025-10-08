package com.itmart.gharkharch.RemoteDataAndApis;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.itmart.gharkharch.DataModels.TokensJSON;
import com.itmart.gharkharch.DataModels.VerifyJSON;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteRepository {

    private final String TAG = "RemoteRepositoryDebugger";
    //    private final String base_url = "https://192.168.29.160:443";
    private final String base_url = "https://premiere-wallace-bali-acquisition.trycloudflare.com";
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static RemoteRepository instance = null;
    private final Handler handler;

    private RemoteRepository() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static synchronized RemoteRepository getInstance() {
        if (instance == null) {
            instance = new RemoteRepository();
        }
        return instance;
    }

    public interface ResCallback<T> {
        void onSuccess(T response);

        void onError(String message);

        void onAccessRevoked();

        void onServerError(String message);
    }

    public void login(String name, String password, Context context, final ResCallback<TokensJSON> callback) {

        Log.i(TAG, "Inside login");

        String url = base_url + "/login";

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("password", password);
        String json = new Gson().toJson(map);

        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .url(url)
                .post(body)
                .build();

        OkHttpClient client = OkHTTPClient.getHttpClient(context);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                if (e instanceof UnknownHostException) {
                    Log.e(TAG, "No internet or bad domain", e);
                } else if (e instanceof SocketTimeoutException) {
                    Log.e(TAG, "Connection timed out", e);
                } else if (e instanceof ConnectException) {
                    Log.e(TAG, "Failed to connect to server", e);
                } else if (e instanceof SSLHandshakeException) {
                    Log.e(TAG, "SSL Handshake failed", e);
                } else {
                    Log.e(TAG, "Unknown network error", e);
                }
                handler.post(() -> callback.onServerError(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                try (response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            try {
                                String json = response.body().string();
                                TokensJSON result = new Gson().fromJson(json, TokensJSON.class);
                                handler.post(() -> callback.onSuccess(result));
                            } catch (Exception e) {
                                Log.i(TAG, "Error while parsing JSON");
                            }
                            //end of 200
                        } else {
                            Log.i(TAG, "Empty res body");
                        }
                    } else if (response.code() == 403) {
                        Log.i(TAG, "Access Denied : " + response.message());
                        handler.post(callback::onAccessRevoked);
                    } else {
                        if (response.body() != null) {
                            handler.post(() -> callback.onError(response.body().toString()));
                        } else {
                            Log.i(TAG, "Empty res body");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error : " + e);
                    handler.post(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }

    public void verify(String token, Context context, final ResCallback<VerifyJSON> callback) {

        Log.i(TAG, "Logging in using access token");

        String url = base_url + "/verify";

        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", token)
                .url(url)
                .get()
                .build();

        OkHttpClient client = OkHTTPClient.getHttpClient(context);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                if (e instanceof UnknownHostException) {
                    Log.e(TAG, "No internet or bad domain", e);
                } else if (e instanceof SocketTimeoutException) {
                    Log.e(TAG, "Connection timed out", e);
                } else if (e instanceof ConnectException) {
                    Log.e(TAG, "Failed to connect to server", e);
                } else if (e instanceof SSLHandshakeException) {
                    Log.e(TAG, "SSL Handshake failed", e);
                } else {
                    Log.e(TAG, "Unknown network error", e);
                }
                handler.post(() -> callback.onServerError(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                try (response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            try {
                                String json = response.body().string();
                                VerifyJSON result = new Gson().fromJson(json, VerifyJSON.class);
                                handler.post(() -> callback.onSuccess(result));
                            } catch (Exception e) {
                                Log.i(TAG, "Error while parsing JSON");
                            }
                        } else {
                            Log.i(TAG, "Empty res body");
                        }
                        //end of 200
                    } else if (response.code() == 403) {
                        Log.i(TAG, "Access Denied : " + response.message());
                        handler.post(callback::onAccessRevoked);
                    } else {
                        Log.i(TAG, "Error code : " + response.code());
                        if (response.body() != null) {
                            handler.post(() -> callback.onError(response.body().toString()));
                        } else {
                            Log.i(TAG, "Empty res body");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error : " + e);
                    handler.post(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }

    public void refresh(String name, String token, Context context, final ResCallback<TokensJSON> callback) {

        Log.i(TAG, "Access token invalidated, refreshing token");

        String url = base_url + "/refresh";

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("refresh_token", token);
        String json = new Gson().toJson(map);

        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        OkHttpClient client = OkHTTPClient.getHttpClient(context);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                if (e instanceof UnknownHostException) {
                    Log.e(TAG, "No internet or bad domain", e);
                } else if (e instanceof SocketTimeoutException) {
                    Log.e(TAG, "Connection timed out", e);
                } else if (e instanceof ConnectException) {
                    Log.e(TAG, "Failed to connect to server", e);
                } else if (e instanceof SSLHandshakeException) {
                    Log.e(TAG, "SSL Handshake failed", e);
                } else {
                    Log.e(TAG, "Unknown network error", e);
                }
                handler.post(() -> callback.onServerError(e.getMessage()));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                try (response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            String json = response.body().string();
                            TokensJSON result = new Gson().fromJson(json, TokensJSON.class);
                            handler.post(() -> callback.onSuccess(result));
                        }
                        //end of 200
                    } else if (response.code() == 403) {
                        Log.i(TAG, "Access Denied : " + response.message());
                        handler.post(callback::onAccessRevoked);
                    } else {
                        if (response.body() != null) {
                            handler.post(() -> callback.onError(response.body().toString()));
                        } else {
                            Log.i(TAG, "Empty res body");
                        }
                    }

                } catch (Exception e) {
                    Log.i(TAG, "Error : " + e);
                    handler.post(() -> callback.onError(e.getMessage()));
                }
            }
        });
    }
}
