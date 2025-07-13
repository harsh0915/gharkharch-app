package com.itmart.gharkharch.Fragments;

import android.content.ContentValues;
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

import DatabaseHelper.DatabaseHelper;
import Utilities.GlobalData;

public class AddCategoryFragment extends Fragment {
    private EditText inputCategory;
    private Button addBtn;
    private DatabaseHelper databaseHelper;
    private GlobalData gdata;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gdata = new GlobalData(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        inputCategory = view.findViewById(R.id.input_category);
        addBtn = view.findViewById(R.id.addBtn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addBtn.setOnClickListener(v -> {
            String new_category = inputCategory.getText().toString();
            if (new_category.isBlank())
                gdata.generateSnackBar(v, "A category name can't be empty!",false);
            else addCategory(new_category);
        });
    }

    private void addCategory(String new_category) {

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
            gdata.generateSnackBar(addBtn, "\"" + new_category + "\" category added!",false);

        } catch (Exception e) {
            Log.d("harshTagDatabaseHelper", " Something went wrong when acquiring database instance. " + e.getMessage());
        }
    }
}
