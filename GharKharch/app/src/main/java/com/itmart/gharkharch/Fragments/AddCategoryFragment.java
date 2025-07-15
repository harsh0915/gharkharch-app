package com.itmart.gharkharch.Fragments;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.itmart.gharkharch.Utilities.GlobalData;

public class AddCategoryFragment extends Fragment implements SimpleListAdapter.DB_Interface {
    private EditText inputCategory;
    private Button addBtn;
    private List<GenericItem> category_list;
    private HashSet<String> category_fast_lookup;
    private RecyclerView rcv_category_list;
    private SimpleListAdapter simple_adapter;
    private DatabaseHelper databaseHelper;
    private TextView page_title;
    private GenericItemType item_type;
    private GlobalData gdata;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gdata = GlobalData.getInstance(requireContext());
        item_type = GenericItemType.Category;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        page_title = requireActivity().findViewById(R.id.page_title);
        inputCategory = view.findViewById(R.id.input_category);
        rcv_category_list = view.findViewById(R.id.rcv_category_list);
        addBtn = view.findViewById(R.id.addBtn);
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inflateRCV();

        addBtn.setOnClickListener(v -> {
           gdata.close_keyboard(v);

            String new_category = inputCategory.getText().toString();
            if (new_category.isBlank())
                gdata.generateSnackBar(v, "A category name can't be empty!");
            else {
                addCategory(new_category);
                fetch_category_list();
                simple_adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        page_title.setText("Expense Category");
    }

    @Override
    public void database_updated() {
        fetch_category_list();
    }

    private void inflateRCV() {
        category_list = new ArrayList<>();
        category_fast_lookup = new HashSet<>();
        fetch_category_list();
        simple_adapter = new SimpleListAdapter(requireContext(), category_list, item_type, this);
        rcv_category_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_category_list.setAdapter(simple_adapter);
    }

    private void addCategory(String new_category) {

        if (category_fast_lookup.contains(new_category)) {
            gdata.generateSnackBar(addBtn, "\"" + new_category + "\" category already exists!");
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
            gdata.generateSnackBar(addBtn, "\"" + new_category + "\" category added!");

        } catch (Exception e) {
            Log.d("harshTagDatabaseHelper", " Something went wrong when acquiring database instance. " + e.getMessage());
        } finally {
            fetch_category_list();
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
}
