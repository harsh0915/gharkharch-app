package com.itmart.gharkharch.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.itmart.gharkharch.R;

import Utilities.GlobalData;

public class BookExpense extends Fragment {

    private TextView modes_of_payment, date_picker;
    private EditText input_sub_category, input_expense;
    private Button addBtn;
    private GlobalData gdata;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gdata = new GlobalData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_expense, container, false);
        input_sub_category = view.findViewById(R.id.input_sub_category);
        input_expense = view.findViewById(R.id.input_expense);
        date_picker = view.findViewById(R.id.date_picker);
        modes_of_payment = view.findViewById(R.id.modes_of_payment);
        addBtn = view.findViewById(R.id.saveBtn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        input_sub_category.addTextChangedListener(new TextWatcher() {
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

        input_expense.addTextChangedListener(new TextWatcher() {
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

        modes_of_payment.setOnClickListener(v -> {
        });

        date_picker.setOnClickListener(v -> {
        });

        addBtn.setOnClickListener(v -> {
            //TODO:some saving logic
            gdata.generateSnackBar(v, "Expense added.",false);
        });
    }
}