package com.itmart.gharkharch.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.itmart.gharkharch.R;

import java.util.ArrayList;
import java.util.List;

import Adapters.CategoryAdapter;
import DatabaseHelper.DatabaseHelper;


public class AddSubCategoryFragment extends Fragment implements CategoryAdapter.CategoryItemClicked {
    private Button saveBtn;
    private EditText inputSubCategory, inputCategory;
    private List<String> category_list;
    private RecyclerView category_search_list;
    private CategoryAdapter category_adapter;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sub_category, container, false);
        inputSubCategory = view.findViewById(R.id.inputSubCategory);
        inputCategory = view.findViewById(R.id.inputCategory);
        saveBtn = view.findViewById(R.id.addBtn);
        category_search_list = view.findViewById(R.id.category_search_list);
        inflate_RCV();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (inputCategory.hasFocus()) inputCategory.clearFocus();
                else {
                    setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });

        saveBtn.setOnClickListener(v -> {
        });

        inputCategory.addTextChangedListener(new TextWatcher() {
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

        inputCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) category_search_list.setVisibility(View.VISIBLE);
            else category_search_list.setVisibility(View.GONE);
        });

    }

    private void inflate_RCV() {
        category_list = new ArrayList<>();
        fetch_category_list();
        category_adapter = new CategoryAdapter(requireContext(), category_list, this);
        category_search_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        category_search_list.setAdapter(category_adapter);
    }

    private void fetch_category_list() {
        try {
            databaseHelper = DatabaseHelper.getInstance(requireContext());
            SQLiteDatabase readable_db = databaseHelper.getReadableDatabase();

            String query = "select " + DatabaseHelper.COLUMN_CATEGORY_NAME + " from " + DatabaseHelper.TABLE_CATEGORIES;
            Cursor cursor = null;

            try {
                cursor = readable_db.rawQuery(query, null);

                if (cursor.moveToFirst()) {
                    do {
                        String cat_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                        category_list.add(cat_name);
                    } while (cursor.moveToNext());
                }
                Log.i("harshTagSqlQuery", "Data retrieved successfully");

            } catch (Exception e) {
                Log.i("harshTagSqlQuery", " Something went wrong when querying the database. Error : " + e.getMessage());
            } finally {
                if (cursor != null) cursor.close();
            }
        } catch (Exception e) {
            Log.i("harshTagDatabaseHelper", " Something went wrong when acquiring database instance. ");
        }

    }

    @Override
    public void on_item_clicked(String cat_name) {
        inputCategory.setText(cat_name);
        inputCategory.setSelection(inputCategory.getText().length());
    }
}