package com.itmart.gharkharch.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.itmart.gharkharch.DataModels.TokensJSON;
import com.itmart.gharkharch.DataModels.VerifyJSON;
import com.itmart.gharkharch.MainActivity;
import com.itmart.gharkharch.R;
import com.itmart.gharkharch.RemoteDataAndApis.RemoteRepository;
import com.itmart.gharkharch.Utilities.Utility_NetworkMonitor;
import com.itmart.gharkharch.Utilities.Utility_UiAlerts;
import com.itmart.gharkharch.Utilities.Utility_UserPreferences;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginDebugger";
    private EditText passwordET, usernameET;
    private Button loginBtn;
    private RemoteRepository remoteRepository;
    private Utility_UserPreferences persistent_storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EdgeToEdge.enable(this);
        initializeHelperClasses();
        initializeUiElements();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!getCurrentNetworkState()) {
            showNoInternetDialog();
            loginBtn.setBackgroundTintList(ContextCompat.getColorStateList(LoginActivity.this, R.color.inactiveButtonColor));
        }
        Utility_NetworkMonitor.registerNetworkCallback(LoginActivity.this, networkMonitorCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Utility_NetworkMonitor.unregisterNetworkCallback(LoginActivity.this, networkMonitorCallback);
    }

    private void setupClickListeners() {

        loginBtn.setOnClickListener(v -> {
            String username = usernameET.getText().toString();
            String password = passwordET.getText().toString();

            if (!password.matches("^[\\x20-\\x7E]{1,72}$")) {
                passwordET.setError("Invalid characters used in password");
                return;
            } else {
                passwordET.setError(null);
            }
            if (username.isEmpty()) {
                usernameET.setError("Empty username");
                return;
            }
            login(username, password);
        });
    }

    private void initializeUiElements() {
        loginBtn = findViewById(R.id.loginBtn);
        passwordET = findViewById(R.id.passwordET);
        usernameET = findViewById(R.id.usernameET);
    }

    private void initializeHelperClasses() {
        remoteRepository = RemoteRepository.getInstance();
        persistent_storage = new Utility_UserPreferences(LoginActivity.this);
    }

    private void autoLogin() {

        if (persistent_storage.getAccessToken() != null) {

            remoteRepository.verify(persistent_storage.getAccessToken(), LoginActivity.this, new RemoteRepository.ResCallback<>() {
                @Override
                public void onSuccess(VerifyJSON response) {
                    if (response.isAccess_granted()) {
                        Utility_UiAlerts.generateToast("Logged in successfully", LoginActivity.this);
                        Intent main = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(main);
                        finish();
                    }
                }

                @Override
                public void onAccessRevoked() {
                    refreshTokens();
                }

                @Override
                public void onError(String e) {
                    Log.e(TAG, "Error : " + e);
                }
            });
        }
    }

    private void login(String username, String password) {

        remoteRepository.login(username, password, this, new RemoteRepository.ResCallback<>() {
            @Override
            public void onSuccess(TokensJSON response) {
                persistent_storage.saveUsername(username);
                persistent_storage.saveTokens(response.getRefresh_token(), response.getAccess_token());
                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }

            @Override
            public void onAccessRevoked() {
                showRevokedDialog();
            }

            @Override
            public void onError(String e) {
                Log.e(TAG, "Error : " + e);
            }
        });
    }

    private void refreshTokens() {

        if (persistent_storage.getRefreshToken() != null && persistent_storage.getUsername() != null) {
            remoteRepository.refresh(persistent_storage.getUsername(), persistent_storage.getRefreshToken(), LoginActivity.this, new RemoteRepository.ResCallback<>() {
                @Override
                public void onSuccess(TokensJSON response) {
                    persistent_storage.saveTokens(response.getRefresh_token(), response.getAccess_token());
                    Utility_UiAlerts.generateToast("Logged in successful", LoginActivity.this);
                    Intent main = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(main);
                    finish();
                }

                @Override
                public void onAccessRevoked() {
                    showRevokedDialog();
                }

                @Override
                public void onError(String e) {
                    Log.e(TAG, "Error : " + e);
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void showRevokedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View layout = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_generic, null);
        builder.setView(layout);

        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        TextView dialog_title = layout.findViewById(R.id.dialog_title);
        TextView dialog_msg = layout.findViewById(R.id.dialog_msg);
        TextView negBtn = layout.findViewById(R.id.negative_btn);
        TextView exitBtn = layout.findViewById(R.id.positive_btn);

        negBtn.setVisibility(View.GONE);
        dialog_title.setText("Subscription Revoked!");
        dialog_msg.setText("Please contact us to renew your subscription");
        exitBtn.setText("Ok");

        exitBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View layout = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dialog_generic, null);
        builder.setView(layout);

        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        TextView dialog_title = layout.findViewById(R.id.dialog_title);
        TextView dialog_msg = layout.findViewById(R.id.dialog_msg);
        TextView negBtn = layout.findViewById(R.id.negative_btn);
        TextView pstBtn = layout.findViewById(R.id.positive_btn);

        negBtn.setVisibility(View.GONE);
        dialog_title.setText("No Internet!");
        dialog_msg.setText("Please make sure you are connected to the internet");
        pstBtn.setText("Ok");

        pstBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.setCancelable(false);
        dialog.show();
    }

    private final ConnectivityManager.NetworkCallback networkMonitorCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            new Handler(Looper.getMainLooper()).post(() -> {
                loginBtn.setBackgroundTintList(ContextCompat.getColorStateList(LoginActivity.this, R.color.buttonColor));
                autoLogin();
            });
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            new Handler(Looper.getMainLooper()).post(() -> {
                showNoInternetDialog();
                loginBtn.setBackgroundTintList(ContextCompat.getColorStateList(LoginActivity.this, R.color.inactiveButtonColor));
            });
        }
    };

    public boolean getCurrentNetworkState() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network activeNetwork = cm.getActiveNetwork();
        NetworkCapabilities caps = cm.getNetworkCapabilities(activeNetwork);
        return (caps != null && (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)));
    }
}