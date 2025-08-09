package com.itmart.gharkharch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.itmart.gharkharch.MainActivity;
import com.itmart.gharkharch.R;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText passwordET, usernameET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EdgeToEdge.enable(this);
        initializeUiElements();
        setupClickListeners();
    }

    private void setupClickListeners() {
        loginBtn.setOnClickListener(v -> {
            verifyUser();
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            //send user details via intent received while confirming details
            startActivity(main);
        });

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        usernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initializeUiElements() {
        loginBtn = findViewById(R.id.loginBtn);
        passwordET = findViewById(R.id.passwordET);
        usernameET = findViewById(R.id.usernameET);
    }

    private void verifyUser() {
    }
}