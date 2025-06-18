package com.itmart.gharkharch.Fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.itmart.gharkharch.R;

import java.util.UUID;

import DatabaseHelper.DatabaseHelper;
import Utilities.GlobalData;

public class AddCategoryFragment extends Fragment {
    private EditText inputCategory;
    private Button addBtn;
    private SQLiteDatabase writeableDb;
    private GlobalData globalData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalData = new GlobalData(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        inputCategory = view.findViewById(R.id.inputCategory);
        addBtn = view.findViewById(R.id.addBtn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addBtn.setOnClickListener(v -> {
            String new_category = inputCategory.getText().toString();
            if (new_category.isBlank())
                globalData.generateSnackBar(v, "Please make sure you provide a name before providing a category!");
            else addCategory(new_category);
        });
    }

    private void addCategory(String new_category) {

        try {
            writeableDb = DatabaseHelper.getInstance(requireContext()).getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, new_category.trim());
            values.put("email", "harsh@example.com");

            long id = writeableDb.insert("users", null, values);
            Log.d("harshTag", "Category added successfully!. Row_ID : " + id);
            inputCategory.setText(null);
            inputCategory.setHint(R.string.hint_category);
        } catch (Exception e) {
            Log.d("harshTag", "Sql error!\nError : " + e.getMessage());
        }
    }
}
