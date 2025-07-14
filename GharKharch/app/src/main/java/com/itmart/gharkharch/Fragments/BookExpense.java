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
import android.widget.Spinner;
import android.widget.TextView;

import com.itmart.gharkharch.R;

import com.itmart.gharkharch.Utilities.GlobalData;

public class BookExpense extends Fragment {

    private TextView date_picker, page_title;
    private EditText input_sub_category, input_expense;
    private Button addBtn;
    private GlobalData gdata;
    private Spinner modes_of_payment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gdata = GlobalData.getInstance(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_expense, container, false);
        page_title = requireActivity().findViewById(R.id.page_title);
        input_sub_category = view.findViewById(R.id.input_sub_category);
        input_expense = view.findViewById(R.id.input_expense);
        date_picker = view.findViewById(R.id.date_picker);
        modes_of_payment = view.findViewById(R.id.modes_of_payment);
        addBtn = view.findViewById(R.id.addBtn);
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

        date_picker.setOnClickListener(v -> {
        });

        addBtn.setOnClickListener(v -> {
            //TODO:some saving logic
            gdata.generateSnackBar(v, "Expense added.");
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        page_title.setText("Book Expense");
    }

//    public void getExpenses() {
//        try {
//            String query = "select * from " + DatabaseHelper.TABLE_EXPENSES;
//            Cursor cursor = readableDb.rawQuery(query, null);
//            do {
//                if (cursor.moveToFirst()) {
//                    int category_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
//                    String category_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
//
//                    Category category = new Category(category_id, category_name);
//                    categoryList.add(category);
//                }
//            } while (cursor.moveToNext());
//
//            cursor.close();
//            Log.d("harshTag", "Successful extraction of data from expenses table!");
//        } catch (Exception e) {
//            Log.d("harshTag", "Sql table creation error!\nError : " + e.getMessage());
//        }
//    }
}