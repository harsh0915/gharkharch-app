package com.itmart.gharkharch;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itmart.gharkharch.DataModels.Category;
import com.itmart.gharkharch.Fragments.AddCategoryFragment;
import com.itmart.gharkharch.Fragments.AddSubCategoryFragment;
import com.itmart.gharkharch.Fragments.BookExpense;

import java.util.HashMap;
import java.util.List;

import DatabaseHelper.DatabaseHelper;
import Utilities.GlobalData;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout fragmentContainer;
    private GlobalData globalData;
    private HashMap<Integer, Fragment> fragmentMap;
    private Fragment currentFragment;
    private SQLiteDatabase readableDb;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readableDb = DatabaseHelper.getInstance(this).getReadableDatabase();
        initializeUiElements();
        setupClickListeners();

//        loads the initial fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_expenses);
            currentFragment = new AddCategoryFragment();
            getSupportFragmentManager().beginTransaction().add(fragmentContainer.getId(), currentFragment).commit();
        }

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                show_quit_confirmation_dialog();
            }
        });
    }

    private void initializeUiElements() {

        globalData = new GlobalData(this);
        fragmentMap = new HashMap<>();
//        views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentContainer = findViewById(R.id.fragmentContainer);

    }

    private void setupClickListeners() {

        bottomNavigationView.setOnItemSelectedListener((item) -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (fragmentMap.containsKey(id)) selectedFragment = fragmentMap.get(id);
            else {
                if (id == R.id.nav_expenses) {
                    selectedFragment = new AddCategoryFragment();
                } else if (item.getItemId() == R.id.nav_analytics) {
                    selectedFragment = new AddSubCategoryFragment();
                } else {
                    selectedFragment = new BookExpense();
                }
                fragmentMap.put(id, selectedFragment);
                getSupportFragmentManager().beginTransaction().add(fragmentContainer.getId(), selectedFragment).hide(selectedFragment).commit();
            }

            if (selectedFragment != null && selectedFragment != currentFragment) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                if (currentFragment != null) {
                    transaction.hide(currentFragment);
                }
                transaction.show(selectedFragment);
                transaction.commit();

                currentFragment = selectedFragment;
            }

            return true;
        });
    }

    public void getExpenses() {
        try {
            String query = "select * from " + DatabaseHelper.TABLE_EXPENSES;
            Cursor cursor = readableDb.rawQuery(query, null);
            do {
                if (cursor.moveToFirst()) {
                    int category_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
                    String category_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                    String user_id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID));

                    Category category = new Category(category_id, category_name, user_id);
                    categoryList.add(category);
                }
            } while (cursor.moveToNext());

            cursor.close();
            Log.d("harshTag", "Successful extraction of data from expenses table!");
        } catch (Exception e) {
            Log.d("harshTag", "Sql table creation error!\nError : " + e.getMessage());
        }
    }


    private void show_quit_confirmation_dialog() {
        globalData.generateSnackBar(bottomNavigationView, "Are you sure you want to quit ghar_kharch?");

    }
}