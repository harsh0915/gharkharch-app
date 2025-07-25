package com.itmart.gharkharch.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.Adapters.SimpleListAdapter;
import com.itmart.gharkharch.DataModels.Category;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.itmart.gharkharch.DatabaseHelper.DatabaseHelper;
import com.itmart.gharkharch.Utilities.Utility_UiAlerts;
import com.itmart.gharkharch.Utilities.Utility_DBOperations;
import com.itmart.gharkharch.Utilities.Utility_Keyboard;

public class AddCategoryFragment extends Fragment implements SimpleListAdapter.SimpleList_Interface {
    private EditText inputCategory;
    private Button addBtn;
    private List<GenericItem> category_list;
    private HashSet<String> category_fast_lookup;
    private RecyclerView rcv_category_list;
    private SimpleListAdapter simple_adapter;
    private DatabaseHelper databaseHelper;
    private TextView page_title;
    private GenericItemType item_type;
    private Utility_UiAlerts utilityUiAlerts;
    private Utility_DBOperations utilityDbOperations;
    private ImageView search_button;
    private SearchView search_view;
    private TextView list_title;
    private ImageView close_button;
    private Utility_Keyboard utilityKeyboard;
    private List<GenericItem> filtered_categories_list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utilityUiAlerts = Utility_UiAlerts.getInstance(requireContext());
        utilityKeyboard = Utility_Keyboard.getInstance(requireContext());
        utilityDbOperations = Utility_DBOperations.getInstance(requireContext());
        item_type = GenericItemType.Category;
        category_list = new ArrayList<>();
        category_fast_lookup = new HashSet<>();
        filtered_categories_list = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        page_title = requireActivity().findViewById(R.id.page_title);
        inputCategory = view.findViewById(R.id.input_category);
        rcv_category_list = view.findViewById(R.id.rcv_category_list);
        addBtn = view.findViewById(R.id.addBtn);
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

        search_button.setOnClickListener(v -> toggle_search_view());

        close_button.setOnClickListener(v -> {
            toggle_search_view();
            search_view.setQuery("", false);
        });

        addBtn.setOnClickListener(v -> {
            utilityKeyboard.close_keyboard(v);

            String new_category = inputCategory.getText().toString();
            if (new_category.trim().isEmpty())
                utilityUiAlerts.generateSnackBar(v, "A category name can't be empty!");
            else {
                addCategory(new_category);
                fetch_category_list();
                simple_adapter.notifyDataSetChanged();
            }
        });

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.toLowerCase();
                filterList(query, filtered_categories_list, category_list);
                return true;
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            page_title.setText("Expense Category");
        }
    }

    @Override
    public void delete_item(int item_id) {
        utilityDbOperations.delete_item(item_id, item_type.getValue(), rcv_category_list);
    }

    @Override
    public void edit_item(GenericItem item, String editedText) {
        if (editedText.isEmpty()) {
            utilityUiAlerts.generateSnackBar(addBtn, "An expense category can't be blank.");
            return;
        }
        utilityDbOperations.edit_category(item.get_item_id(), editedText);
    }

    @Override
    public void database_updated() {
        fetch_category_list();
    }

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
        fetch_category_list();
        simple_adapter = new SimpleListAdapter(requireContext(), filtered_categories_list, item_type, this);
        rcv_category_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_category_list.setAdapter(simple_adapter);
    }

    private void addCategory(String new_category) {

        if (category_fast_lookup.contains(new_category)) {
            utilityUiAlerts.generateSnackBar(addBtn, "\"" + new_category + "\" category already exists!");
            return;
        }

        try {
            databaseHelper = DatabaseHelper.getInstance(requireContext());
            SQLiteDatabase writeable_db = databaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, new_category.trim());

            try {
                long id = writeable_db.insert(DatabaseHelper.TABLE_CATEGORIES, null, values);

                if (id == -1)
                    Log.d("harshTagSqlQuery", "Something went wrong while updating the table. Rows updated : " + id);
                else Log.d("harshTagSqlQuery", "Rows updated successfully. Rows updated : " + id);

            } catch (Exception e) {
                Log.i("harshTagSqlQuery", " Something went wrong when querying the database. Error : " + e.getMessage());
            }

            inputCategory.setText(null);
            inputCategory.setHint(R.string.hint_category);
            utilityUiAlerts.generateSnackBar(addBtn, "\"" + new_category + "\" category added!");

        } catch (Exception e) {
            Log.d("harshTagDatabaseHelper", " Something went wrong when acquiring database instance. " + e.getMessage());
        } finally {
            fetch_category_list();
        }
    }

    public void fetch_category_list() {
        filtered_categories_list.clear();
        category_list.clear();
        category_fast_lookup.clear();
        try {
            databaseHelper = DatabaseHelper.getInstance(requireContext());
            SQLiteDatabase readable_db = databaseHelper.getReadableDatabase();

            String query = "select * from " + DatabaseHelper.TABLE_CATEGORIES;
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
                        filtered_categories_list.add(category);
                    } while (cursor.moveToNext());
                }
                Log.i("harshTagSqlQuery", "Data retrieved successfully");

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
    private void filterList(String query, List<GenericItem> list_toBeFiltered, List<GenericItem> filterFrom) {
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
        simple_adapter.notifyDataSetChanged();
    }
}
