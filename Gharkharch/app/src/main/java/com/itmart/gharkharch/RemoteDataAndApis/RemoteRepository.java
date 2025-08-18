package com.itmart.gharkharch.RemoteDataAndApis;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.itmart.gharkharch.DataModels.TokensJSON;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemoteRepository {

    private static final String base_url = "https://192.168.29.160:443";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static RemoteRepository instance = null;

    private RemoteRepository() {
    }

    public static RemoteRepository getInstance() {
        if (instance == null) {
            instance = new RemoteRepository();
        }
        return instance;
    }

    public interface ResCallback {
        void onSuccess(TokensJSON response);

        void onError(Exception e);
    }

    public void login(String name, String password, Context context, final ResCallback callback) {

        String url = base_url + "/login";


        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("password", password);
        String json = new Gson().toJson(map);

        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        OkHttpClient client = OkHTTPClient.getHttpClient(context);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                Log.i("harshRemoteData", "=>" + Log.getStackTraceString(e));
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                try (response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            String json = response.body().string();
                            TokensJSON result = new Gson().fromJson(json, TokensJSON.class);
                            //passing to callback to the main looper is a clean to avoid ui thread exceptions
                            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
                        }
                    } else {
                        if (response.code() == 401) {
                            //go call the refresh function
                        } else {
                            IOException error = new IOException("HTTP error: " + response.code());
                            new Handler(Looper.getMainLooper()).post(() -> callback.onError(error));
                        }
                    }
                } catch (Exception e) {
                    Log.i("harshRemoteData", "=>" + e.getMessage());
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
                }
            }
        });
    }

    public void verify(String token, Context context, final ResCallback callback) {
        String url = base_url + "/verify";


        Request request = new Request.Builder()
                .addHeader("Authorization", token)
                .header("Content-Type", "application/json")
                .url(url)
                .build();

        OkHttpClient client = OkHTTPClient.getHttpClient(context);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                Log.i("harshRemoteData", "=>" + Log.getStackTraceString(e));
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                try (response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            String json = response.body().string();
                            TokensJSON result = new Gson().fromJson(json, TokensJSON.class);
                            //passing to callback to the main looper is a clean to avoid ui thread exceptions
                            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
                        }
                    } else {
                        if (response.code() == 401) {
                            //go call the refresh function
                        } else {
                            IOException error = new IOException("HTTP error: " + response.code());
                            new Handler(Looper.getMainLooper()).post(() -> callback.onError(error));
                        }
                    }
                } catch (Exception e) {
                    Log.i("harshRemoteData", "=>" + e.getMessage());
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
                }
            }
        });
    }

    public void refresh(String name, String token, Context context, final ResCallback callback) {

        String url = base_url + "/refresh";


        HashMap<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("password", token);
        String json = new Gson().toJson(map);

        RequestBody body = RequestBody.create(json, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        OkHttpClient client = OkHTTPClient.getHttpClient(context);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                Log.i("harshRemoteData", "=>" + Log.getStackTraceString(e));
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                try (response) {
                    if (response.code() == 200) {
                        if (response.body() != null) {
                            String json = response.body().string();
                            TokensJSON result = new Gson().fromJson(json, TokensJSON.class);
                            //passing to callback to the main looper is a clean to avoid ui thread exceptions
                            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
                        }
                    } else {
                        if (response.code() == 401) {
                            //go call the refresh function
                        } else {
                            IOException error = new IOException("HTTP error: " + response.code());
                            new Handler(Looper.getMainLooper()).post(() -> callback.onError(error));
                        }
                    }
                } catch (Exception e) {
                    Log.i("harshRemoteData", "=>" + e.getMessage());
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
                }
            }
        });
    }
}
