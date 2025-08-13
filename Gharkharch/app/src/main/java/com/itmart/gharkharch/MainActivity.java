package com.itmart.gharkharch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.itmart.gharkharch.Activities.AnalyseExpensesActivity;
import com.itmart.gharkharch.Activities.EditProfileActivity;
import com.itmart.gharkharch.Activities.ManageExpensesActivity;
import com.itmart.gharkharch.Activities.BackupDataActivity;
import com.itmart.gharkharch.Activities.RestoreDataActivity;
import com.itmart.gharkharch.Fragments.AddCategoryFragment;
import com.itmart.gharkharch.Fragments.AddSubCategoryFragment;
import com.itmart.gharkharch.Fragments.BookExpenseFragment;
import com.itmart.gharkharch.Utilities.Utility_UiAlerts;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout fragmentContainer;
    private HashMap<Integer, Fragment> fragmentMap;
    private Fragment currentFragment;
    private ImageView menu_button;
    private DrawerLayout landing_page;
    private FrameLayout drawer_panel;
    private LinearLayout content_panel;
    private Utility_UiAlerts utilityUiAlerts;
    //drawer elements
    private TextView profile;
    private TextView manage_expenses;
    private TextView analyse_expenses;
    private TextView backup_data;
    private TextView restore_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUiElements();
        setupClickListeners();

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_addCategory);
            currentFragment = new AddCategoryFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(fragmentContainer.getId(), currentFragment)
                    .hide(currentFragment)
                    .commit();
        }

        if (savedInstanceState != null) {
            bottomNavigationView.setSelectedItemId(savedInstanceState.getInt("selectedItem"));
        } else {
            bottomNavigationView.setSelectedItemId(R.id.nav_addCategory);
        }

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                show_quit_confirmation_dialog();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedItem", bottomNavigationView.getSelectedItemId());
    }

    private void initializeUiElements() {
        fragmentMap = new HashMap<>();
        utilityUiAlerts = Utility_UiAlerts.getInstance(this);
        //views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        menu_button = findViewById(R.id.menu_button);
        landing_page = findViewById(R.id.landing_page);
        drawer_panel = findViewById(R.id.drawer_panel);
        content_panel = findViewById(R.id.content_panel);
        //drawer elements
        profile = findViewById(R.id.profile);
        manage_expenses = findViewById(R.id.manage_expenses);
        analyse_expenses = findViewById(R.id.analyse_expenses);
        backup_data = findViewById(R.id.backup_data);
        restore_data = findViewById(R.id.restore_data);
    }

    private void setupClickListeners() {

        bottomNavigationView.setOnItemSelectedListener((item) -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (fragmentMap.containsKey(id)) selectedFragment = fragmentMap.get(id);
            else {
                if (id == R.id.nav_addCategory) {
                    selectedFragment = new AddCategoryFragment();
                } else if (item.getItemId() == R.id.nav_addSubCategory) {
                    selectedFragment = new AddSubCategoryFragment();
                } else {
                    selectedFragment = new BookExpenseFragment();
                }
                fragmentMap.put(id, selectedFragment);
                getSupportFragmentManager()
                        .beginTransaction().add(fragmentContainer.getId(), selectedFragment)
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

        menu_button.setOnClickListener(v -> {
            if (!landing_page.isDrawerOpen(drawer_panel))
                landing_page.openDrawer(GravityCompat.END);
            else
                landing_page.closeDrawer(GravityCompat.END);
        });

        landing_page.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Move content as drawer slides
                float moveFactor = drawerView.getWidth() * slideOffset;
                content_panel.setTranslationX(-moveFactor);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {

                if (
                        (newState == DrawerLayout.STATE_DRAGGING)
                                || (newState == DrawerLayout.STATE_IDLE && landing_page.isDrawerOpen(drawer_panel))
                                || (newState == DrawerLayout.STATE_SETTLING)
                ) {
                    menu_button.setImageResource(R.drawable.menu_icon_active);
                } else {
                    menu_button.setImageResource(R.drawable.menu_icon);
                }
            }

        });

        profile.setOnClickListener(v -> {
            utilityUiAlerts.generateToast("Feature under development");
//            Intent edit_profile = new Intent(MainActivity.this, EditProfileActivity.class);
//            startActivity(edit_profile);
        });

        manage_expenses.setOnClickListener(v -> {
//            utilityUiAlerts.generateToast("Feature under development");
            Intent manage_expenses = new Intent(MainActivity.this, ManageExpensesActivity.class);
            startActivity(manage_expenses);
        });

        analyse_expenses.setOnClickListener(v -> {
            Intent analyse_expenses = new Intent(MainActivity.this, AnalyseExpensesActivity.class);
            startActivity(analyse_expenses);
        });

        backup_data.setOnClickListener(v -> {
            utilityUiAlerts.generateToast("Feature under development");
//            Intent settings = new Intent(MainActivity.this, BackupDataActivity.class);
//            startActivity(settings);
        });

        restore_data.setOnClickListener(v -> {
            utilityUiAlerts.generateToast("Feature under development");
//            Intent edit_profile = new Intent(MainActivity.this, RestoreDataActivity.class);
//            startActivity(edit_profile);
        });

    }

    private void show_quit_confirmation_dialog() {
        Snackbar snackbar = Snackbar.make(bottomNavigationView, "Are you sure you want to quit?", Snackbar.LENGTH_LONG);
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        TextView actionable_textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_action);

        textView.setTextColor(getColor(R.color.black));
        snackbar.setBackgroundTint(getColor(R.color.white));
        actionable_textView.setTextColor(getColor(R.color.red));

        snackbar.setAction("Quit", v -> {
            snackbar.dismiss();
            finishAffinity();
        });
        snackbar.show();
    }

}