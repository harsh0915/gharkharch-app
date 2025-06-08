package com.itmart.gharkharch;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itmart.gharkharch.Fragments.AnalyticsFragment;
import com.itmart.gharkharch.Fragments.ExpensesFragment;
import com.itmart.gharkharch.Fragments.SettingsFragment;

import java.util.HashMap;
import java.util.List;

import DatabaseHelper.GlobalData;

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

        //loads the initial fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_expenses);

            currentFragment = ExpensesFragment.newInstance(expensesData);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(fragmentContainer.getId(), currentFragment)
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
                    selectedFragment = ExpensesFragment.newInstance(expensesData);
                } else if (item.getItemId() == R.id.nav_analytics) {
                    selectedFragment = AnalyticsFragment.newInstance("hello", "world");
                } else {
                    selectedFragment = SettingsFragment.newInstance("hello", "world");
                }
                fragmentMap.put(id, selectedFragment);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(fragmentContainer.getId(), selectedFragment)
                        .hide(selectedFragment)
                        .commit();
            }

            if (selectedFragment != null && selectedFragment != currentFragment) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(currentFragment)
                        .show(selectedFragment)
                        .commit();

                currentFragment = selectedFragment;
            }

            return true;
        });
    }
}