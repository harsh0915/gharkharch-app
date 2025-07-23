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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.itmart.gharkharch.Adapters.SimpleListAdapter;
import com.itmart.gharkharch.DataModels.Category;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.DataModels.SubCategory;
import com.itmart.gharkharch.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.itmart.gharkharch.Adapters.SearchListAdapter;

import com.itmart.gharkharch.DatabaseHelper.DatabaseHelper;
import com.itmart.gharkharch.Utilities.GlobalData;


public class AddSubCategoryFragment extends Fragment implements SearchListAdapter.ItemClicked, SimpleListAdapter.SimpleList_Interface {
    private Button addBtn;
    private EditText inputSubCategory, inputCategory;
    private List<GenericItem> category_search_list, sub_category_list;
    private HashSet<String> category_fast_lookup;
    private RecyclerView rcv_category_search;
    private SearchListAdapter search_adapter;
    private DatabaseHelper databaseHelper;
    private int under_category_id;
    private TextView page_title;
    private SimpleListAdapter simple_adapter;
    private RecyclerView rcv_sub_category_list;
    private GenericItemType item_type;
    private GlobalData gdata;
    private ImageView search_button;
    private SearchView search_view;
    private TextView list_title;
    private ImageView close_button;
    private List<GenericItem> filtered_search_list, filtered_sub_categories;

    private enum ListType {
        Search,
        Simple
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item_type = GenericItemType.Sub_Category;
        gdata = GlobalData.getInstance(requireContext());
        databaseHelper = DatabaseHelper.getInstance(requireContext());
        filtered_search_list = new ArrayList<>();
        sub_category_list = new ArrayList<>();
        category_search_list = new ArrayList<>();
        category_fast_lookup = new HashSet<>();
        filtered_sub_categories = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sub_category, container, false);
        page_title = requireActivity().findViewById(R.id.page_title);
        inputSubCategory = view.findViewById(R.id.input_sub_category);
        inputCategory = view.findViewById(R.id.input_category);
        addBtn = view.findViewById(R.id.addBtn);
        rcv_category_search = view.findViewById(R.id.category_search_list);
        rcv_sub_category_list = view.findViewById(R.id.rcv_sub_category_list);
        search_button = view.findViewById(R.id.search_button);
        search_view = view.findViewById(R.id.search_view);
        list_title = view.findViewById(R.id.list_title);
        close_button = view.findViewById(R.id.close_button);
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inflate_category_RCV();
        inflate_sub_category_RCV();

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

        //TODO:bind the search list adapter with the input text
        inputCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rcv_category_search.setVisibility(View.VISIBLE);
                String query = s.toString().toLowerCase();
                filterList(query, filtered_search_list, category_search_list, ListType.Search);
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

        search_button.setOnClickListener(v -> toggle_search_view());

        close_button.setOnClickListener(v -> {
            toggle_search_view();
            search_view.setQuery("", false);
        });

        addBtn.setOnClickListener(v -> {
            gdata.close_keyboard(v);

            String sub_cat_name = inputSubCategory.getText().toString(), cat_name = inputCategory.getText().toString();
            if (sub_cat_name.isEmpty()) {
                gdata.generateSnackBar(addBtn, "A sub-category name can't be empty!");
                return;
            } else if (cat_name.isEmpty()) {
                gdata.generateSnackBar(addBtn, "A sub-category can't be added without a category!");
                return;
            } else if (!category_fast_lookup.contains(cat_name)) {
                gdata.generateSnackBar(addBtn, "Please add the \"" + cat_name + "\" category before using it. ");
                return;
            } else addSubCategory(sub_cat_name, under_category_id);

            fetch_sub_category_list();
            simple_adapter.notifyDataSetChanged();
            rcv_category_search.setVisibility(View.GONE);
        });

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.toLowerCase();
                filterList(query, filtered_sub_categories, sub_category_list, ListType.Simple);
                return true;
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //refresh list when coming from another frag as it may have added another data set
            fetch_category_search_list();
            search_adapter.notifyDataSetChanged();
            page_title.setText("Expense Heads");
        }
    }

    @Override
    public void delete_item(int item_id) {
        gdata.delete_item(item_id, item_type.getValue(), rcv_sub_category_list);
    }

    //beginning of callbacks
    @Override
    public void edit_item(GenericItem item, String editedText) {
        if (editedText.isEmpty()) {
            gdata.generateSnackBar(addBtn, "An expense head can't be blank.");
            return;
        }
        gdata.edit_sub_category(item.get_item_id(), editedText);
    }

    @Override
    public void database_updated() {
        fetch_sub_category_list();
    }

    @Override
    public void on_item_clicked(GenericItem clicked_item) {
        inputCategory.setText(clicked_item.get_item_name());
        under_category_id = clicked_item.get_item_id();
        //this line moves the cursor to the end of the text inside the edi text
        inputCategory.setSelection(inputCategory.getText().length());
        rcv_category_search.setVisibility(View.GONE);
    }
    //ending of callbacks

    public void toggle_search_view() {
        if (search_button.getVisibility() == View.VISIBLE) {
            search_view.setVisibility(View.VISIBLE);
            close_button.setVisibility(View.VISIBLE);
            search_button.setVisibility(View.GONE);
            list_title.setVisibility(View.GONE);
        } else {
            search_view.setVisibility(View.GONE);
            close_button.setVisibility(View.GONE);
            search_button.setVisibility(View.VISIBLE);
            list_title.setVisibility(View.VISIBLE);
        }
    }

    private void inflate_category_RCV() {
        fetch_category_search_list();
        search_adapter = new SearchListAdapter(requireContext(), filtered_search_list, this);
        rcv_category_search.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_category_search.setAdapter(search_adapter);
    }

    private void inflate_sub_category_RCV() {
        fetch_sub_category_list();
        simple_adapter = new SimpleListAdapter(requireContext(), filtered_sub_categories, item_type, this);
        rcv_sub_category_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_sub_category_list.setAdapter(simple_adapter);
    }

    private void addSubCategory(String new_sub_category, int under_cat_id) {

        try {
            SQLiteDatabase writeable_db = databaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME, new_sub_category.trim());
            values.put(DatabaseHelper.COLUMN_UNDER_CATEGORY, under_cat_id);

            try {
                long id = writeable_db.insert(DatabaseHelper.TABLE_SUB_CATEGORIES, null, values);

                if (id == -1) {
                    Log.d("harshTagSqlQuery", "Something went wrong while updating the sub category table. Rows updated : " + id);
                    gdata.generateSnackBar(addBtn, "\"" + new_sub_category + "\" sub-category already exists!!");
                } else {
                    Log.d("harshTagSqlQuery", "Sub-category table rows updated successfully. Rows updated : " + id);
                    gdata.generateSnackBar(addBtn, "\"" + new_sub_category + "\" sub-category added.");
                }
            } catch (Exception e) {
                Log.i("harshTagSqlQuery", " Something went wrong when querying the database. Error : " + e.getMessage());
            }
            Log.i("harshTagFK", "Under cat id when adding :" + under_cat_id);
            inputCategory.setText("");
            inputSubCategory.setText("");
            inputCategory.setHint(R.string.hint_category);
            inputSubCategory.setHint(R.string.hint_sub_category);
        } catch (Exception e) {
            Log.d("harshTagDatabaseHelper", " Something went wrong when acquiring database instance. " + e.getMessage());
        }
    }

    public void fetch_category_search_list() {
        filtered_search_list.clear();
        category_search_list.clear();
        category_fast_lookup.clear();
        try {
            SQLiteDatabase readable_db = databaseHelper.getReadableDatabase();

            String query = "select * from " + DatabaseHelper.TABLE_CATEGORIES;
            Cursor cursor = null;

            try {
                cursor = readable_db.rawQuery(query, null);

                if (cursor.moveToFirst()) {
                    do {
                        int cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
                        String cat_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                        Category category = new Category(cat_id, cat_name);
                        category_search_list.add(category);
                        category_fast_lookup.add(cat_name);
                        filtered_search_list.add(category);
                    } while (cursor.moveToNext());
                }

            } catch (Exception e) {
                Log.e("harshTagSqlQuery", " Something went wrong when querying the database. Error : " + e.getMessage());
            } finally {
                if (cursor != null) cursor.close();
            }
        } catch (Exception e) {
            Log.e("harshTagDatabaseHelper", " Something went wrong when acquiring database instance. " + e.getMessage());
        }
    }

    public void fetch_sub_category_list() {
        filtered_sub_categories.clear();
        sub_category_list.clear();
        try {
            SQLiteDatabase readable_db = databaseHelper.getReadableDatabase();

            String query = "select * from " + DatabaseHelper.TABLE_SUB_CATEGORIES;
            Cursor cursor = null;

            try {
                cursor = readable_db.rawQuery(query, null);

                if (cursor.moveToFirst()) {
                    do {
                        int sub_cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUB_CATEGORY_ID));
                        String sub_cat_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME));
                        int cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UNDER_CATEGORY));

                        SubCategory subCategory = new SubCategory(sub_cat_id, sub_cat_name, cat_id);
                        sub_category_list.add(subCategory);
                        filtered_sub_categories.add(subCategory);
                    } while (cursor.moveToNext());
                }

            } catch (Exception e) {
                Log.e("harshTagSqlQuery", " Something went wrong when querying the database. Error : " + e.getMessage());
            } finally {
                if (cursor != null) cursor.close();
            }
        } catch (Exception e) {
            Log.e("harshTagDatabaseHelper", " Something went wrong when acquiring database instance." + e.getMessage());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterList(String query, List<GenericItem> list_toBeFiltered, List<GenericItem> filterFrom, ListType list_type) {
        list_toBeFiltered.clear();

        if (query.isEmpty()) {
            // If query is empty, show all names
            list_toBeFiltered.addAll(filterFrom);
        } else {
            // Iterate through the original, sorted list
            for (GenericItem item : filterFrom) {
                if (item.get_item_name().toLowerCase().startsWith(query)) {
                    list_toBeFiltered.add(item);
                }
                // Optional: Performance optimization for very large lists and specific criteria
                // If checking startsWith and the list is sorted, you could potentially break early
                // if the current name's first character is greater than the query's first character.
                // However, for typical prefix matching, it's often simpler to just iterate.
            }
        }
        if (list_type == ListType.Search)
            search_adapter.notifyDataSetChanged();
        else
            simple_adapter.notifyDataSetChanged();
    }
}