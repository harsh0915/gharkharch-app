package com.itmart.gharkharch.RemoteDataAndApis;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itmart.gharkharch.DataModels.SubCategory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class RemoteData {
    public interface ResCallback <T>{
        void onSuccess(T response);

        void onError(Exception e);
    }

    public static <T> void get(Context context,final ResCallback<T> callback) {
        Log.i("harshRemoteData", "inside the func");
        String url = "https://192.168.29.160:443/sub_categories";

        OkHttpClient client = OkHTTPClient.getHttpClient(context);

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                Log.i("harshRemoteData", "=>" + Log.getStackTraceString(e));
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) {
                try (response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Type listType = new TypeToken<List<SubCategory>>(){}.getType();
                            T result = new Gson().fromJson(response.body().charStream(), listType);
                            //passing to callback to the main looper is a clean to avoid ui thread exceptions
                            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
                        }
                    } else {
                        IOException error = new IOException("HTTP error: " + response.code());
                        Log.i("harshRemoteData", "=>" + error);
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError(error));
                    }
                } catch (Exception e) {
                    Log.i("harshRemoteData", "=>" + e.getMessage());
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(e));
                }
            }
        });
    }
}
