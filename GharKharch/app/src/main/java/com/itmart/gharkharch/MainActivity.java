package com.itmart.gharkharch;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itmart.gharkharch.Fragments.AddCategoryFragment;
import com.itmart.gharkharch.Fragments.AddSubCategoryFragment;
import com.itmart.gharkharch.Fragments.AnalyticsFragment;
import com.itmart.gharkharch.Fragments.BookExpense;
import com.itmart.gharkharch.Fragments.ExpensesFragment;
import com.itmart.gharkharch.Fragments.SettingsFragment;

import java.util.HashMap;
import java.util.List;

import Utilities.GlobalData;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FrameLayout fragmentContainer;
    GlobalData globalData;
    List<Integer> expensesData;
    HashMap<Integer, Fragment> fragmentMap;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUiElements();
        setupClickListeners();

//        loads the initial fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_expenses);
            currentFragment= new AddCategoryFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(fragmentContainer.getId(), currentFragment)
                    .commit();
        }
    }

    private void initializeUiElements() {

        globalData = new GlobalData(this);
        expensesData = globalData.getExpensesData();
        fragmentMap = new HashMap<>();
        //views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentContainer = findViewById(R.id.fragmentContainer);

    }

    private void setupClickListeners() {

        bottomNavigationView.setOnItemSelectedListener((item) -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (fragmentMap.containsKey(id))
                selectedFragment = fragmentMap.get(id);
            else {
                if (id == R.id.nav_expenses) {
                    selectedFragment = new AddCategoryFragment();
                } else if (item.getItemId() == R.id.nav_analytics) {
                    selectedFragment = new AddSubCategoryFragment();
                } else {
                    selectedFragment = new BookExpense();
                }
                fragmentMap.put(id, selectedFragment);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(fragmentContainer.getId(), selectedFragment)
                        .hide(selectedFragment)
                        .commit();
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
}