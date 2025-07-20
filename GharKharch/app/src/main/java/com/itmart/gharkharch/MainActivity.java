package com.itmart.gharkharch;

import android.database.sqlite.SQLiteDatabase;
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
import com.itmart.gharkharch.DataModels.Category;
import com.itmart.gharkharch.Fragments.AddCategoryFragment;
import com.itmart.gharkharch.Fragments.AddSubCategoryFragment;
import com.itmart.gharkharch.Fragments.BookExpense;

import java.util.HashMap;
import java.util.List;

import com.itmart.gharkharch.DatabaseHelper.DatabaseHelper;
import com.itmart.gharkharch.Utilities.GlobalData;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout fragmentContainer;
    private GlobalData globalData;
    private HashMap<Integer, Fragment> fragmentMap;
    private Fragment currentFragment;
    private SQLiteDatabase readableDb;
    private List<Category> categoryList;
    private ImageView menu_button;
    private DrawerLayout landing_page;
    private FrameLayout drawer_panel;
    private LinearLayout content_panel;
    //drawer elements
    private TextView edit_profile, change_themes, manage_expenses, analyse_expenses, clear_all_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        readableDb = DatabaseHelper.getInstance(this).getReadableDatabase();
        initializeUiElements();
        setupClickListeners();

//        loads the initial fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_add_category);
            currentFragment = new AddCategoryFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(fragmentContainer.getId(), currentFragment)
                    .hide(currentFragment)
                    .commit();
        }
        bottomNavigationView.setSelectedItemId(R.id.nav_add_category);

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                show_quit_confirmation_dialog();
            }
        });
    }

    private void initializeUiElements() {

        globalData = GlobalData.getInstance(this);
        fragmentMap = new HashMap<>();
        //views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentContainer = findViewById(R.id.fragmentContainer);
        menu_button = findViewById(R.id.menu_button);
        landing_page = findViewById(R.id.landing_page);
        drawer_panel = findViewById(R.id.drawer_panel);
        content_panel = findViewById(R.id.content_panel);
        //drawer elements
        edit_profile = findViewById(R.id.edit_profile);
        change_themes = findViewById(R.id.change_themes);
        manage_expenses = findViewById(R.id.manage_expenses);
        analyse_expenses = findViewById(R.id.analyse_expenses);
        clear_all_data = findViewById(R.id.clear_all_data);
    }

    private void setupClickListeners() {

        bottomNavigationView.setOnItemSelectedListener((item) -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (fragmentMap.containsKey(id)) selectedFragment = fragmentMap.get(id);
            else {
                if (id == R.id.nav_add_category) {
                    selectedFragment = new AddCategoryFragment();
                } else if (item.getItemId() == R.id.nav_add_sub_category) {
                    selectedFragment = new AddSubCategoryFragment();
                } else {
                    selectedFragment = new BookExpense();
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

                //checks if drawer is being opened applies a blur effect
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

        manage_expenses.setOnClickListener(v->{

        });
    }

    private void show_quit_confirmation_dialog() {
        Snackbar snackbar = Snackbar.make(bottomNavigationView, "Are you sure you want to quit Gharkharch?", Snackbar.LENGTH_LONG);
        snackbar.setAction("Ok", v -> {
            snackbar.dismiss();
            finishAffinity();
        });
        snackbar.show();
    }
}