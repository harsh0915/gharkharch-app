package com.itmart.gharkharch.Fragments;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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

import com.itmart.gharkharch.DataModels.Category;
import com.itmart.gharkharch.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.itmart.gharkharch.Adapters.SearchListAdapter;

import DatabaseHelper.DatabaseHelper;
import Utilities.GlobalData;


public class AddSubCategoryFragment extends Fragment implements SearchListAdapter.ItemClicked {
    private Button addBtn;
    private EditText inputSubCategory, inputCategory;
    private List<SearchListAdapter.SearchableItem> category_list;
    private HashSet<String> category_fast_lookup;
    private RecyclerView rcv_category_search;
    private SearchListAdapter search_adapter;
    private DatabaseHelper databaseHelper;
    private int under_category_id;
    private GlobalData gdata;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gdata = new GlobalData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sub_category, container, false);
        inputSubCategory = view.findViewById(R.id.input_sub_category);
        inputCategory = view.findViewById(R.id.input_category);
        addBtn = view.findViewById(R.id.addBtn);
        rcv_category_search = view.findViewById(R.id.category_search_list);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inflate_RCV();

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

        addBtn.setOnClickListener(v -> {
            String sub_cat_name = inputSubCategory.getText().toString(), cat_name = inputCategory.getText().toString();
            if (!category_fast_lookup.contains(cat_name)) {
                gdata.generateSnackBar(addBtn, "Please add the \"" + cat_name + "\" category before using it. ", false);
                return;
            }
            if (sub_cat_name.isEmpty())
                gdata.generateSnackBar(addBtn, "A category name can't be empty!", false);
            else if (cat_name.isEmpty())
                gdata.generateSnackBar(addBtn, "A sub-category can't be added without a category!", false);
            else addSubCategory(sub_cat_name, cat_name, under_category_id);
        });

        //TODO:bind the search list adapter with the input text
        inputCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rcv_category_search.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                rcv_category_search.setVisibility(View.VISIBLE);
            } else {
                rcv_category_search.setVisibility(View.GONE);
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            fetch_category_list();
        }
    }

    private void inflate_RCV() {
        category_list = new ArrayList<>();
        category_fast_lookup = new HashSet<>();
        fetch_category_list();
        search_adapter = new SearchListAdapter(requireContext(), category_list, this);
        rcv_category_search.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_category_search.setAdapter(search_adapter);
    }

    private void addSubCategory(String new_sub_category, String under_category, int under_cat_id) {

        try {
            databaseHelper = DatabaseHelper.getInstance(requireContext());
            SQLiteDatabase writeable_db = databaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME, new_sub_category.trim());
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, under_category);
            values.put(DatabaseHelper.COLUMN_CATEGORY_ID, under_cat_id);

            try {
                long id = writeable_db.insert(DatabaseHelper.TABLE_SUBCATEGORIES, null, values);

                if (id == -1){
                    Log.d("harshTagSqlQuery", "Something went wrong while updating the sub category table. Rows updated : " + id);
                    gdata.generateSnackBar(addBtn, "\"" + new_sub_category + "\" category already exists!!", false);
                }
                else {
                    Log.d("harshTagSqlQuery", "Sub-category table rows updated successfully. Rows updated : " + id);
                    gdata.generateSnackBar(addBtn, "\"" + new_sub_category + "\" sub-category added.", false);
                }
            } catch (Exception e) {
                Log.i("harshTagSqlQuery", " Something went wrong when querying the database. Error : " + e.getMessage());
            }

            inputCategory.setText("");
            inputSubCategory.setText("");
            inputCategory.setHint(R.string.hint_category);
            inputSubCategory.setHint(R.string.hint_sub_category);
        } catch (Exception e) {
            Log.d("harshTagDatabaseHelper", " Something went wrong when acquiring database instance. " + e.getMessage());
        }
    }

    public void fetch_category_list() {
        category_list.clear();
        category_fast_lookup.clear();
        try {
            databaseHelper = DatabaseHelper.getInstance(requireContext());
            SQLiteDatabase readable_db = databaseHelper.getReadableDatabase();

            String query = "select " + DatabaseHelper.COLUMN_CATEGORY_NAME + " , " + DatabaseHelper.COLUMN_CATEGORY_ID + " from " + DatabaseHelper.TABLE_CATEGORIES;
            Cursor cursor = null;

            try {
                cursor = readable_db.rawQuery(query, null);

                if (cursor.moveToFirst()) {
                    do {
                        String cat_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                        int cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
                        Category category = new Category(cat_id, cat_name);
                        category_list.add(category);
                        category_fast_lookup.add(cat_name);
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
        Log.i("harshTagFetching", " fetching ");
    }

    @Override
    public void on_item_clicked(SearchListAdapter.SearchableItem clicked_item) {
        inputCategory.setText(clicked_item.get_item_name());
        under_category_id = clicked_item.get_item_id();
        inputCategory.setSelection(inputCategory.getText().length());
        rcv_category_search.setVisibility(View.GONE);
    }
}