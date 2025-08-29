package com.itmart.gharkharch.Activities;

import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.ERROR;
import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.SUCCESS;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.Adapters.ManageExpensesAdapter;
import com.itmart.gharkharch.DataModels.DatabaseCallbackObject;
import com.itmart.gharkharch.DataModels.ExpenseItem;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.Fragments.EditExpenseFragment;
import com.itmart.gharkharch.R;
import com.itmart.gharkharch.Utilities.Utility_Calendar;
import com.itmart.gharkharch.Utilities.Utility_DBOperations;
import com.itmart.gharkharch.Utilities.Utility_Keyboard;
import com.itmart.gharkharch.Utilities.Utility_MonthHeaderRCV;
import com.itmart.gharkharch.Utilities.Utility_UiAlerts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ManageExpensesActivity extends AppCompatActivity implements ManageExpensesAdapter.Click_Interface {

    private Utility_DBOperations utilityDbOperations;
    private Utility_Keyboard utilityKeyboard;
    private ManageExpensesAdapter expense_adapter;
    private List<GenericItem> expense_list;
    private ImageView search_button;
    private SearchView search_view;
    private TextView list_title;
    private ImageView close_button;
    private List<GenericItem> filtered_expenses;
    private RecyclerView rcv_expense_list;
    private Calendar startDate;
    private Calendar endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_expenses);
        initializeDataStructures();
        initializeUiElements();
        setupEventListeners();
        fetchData();
    }

    @Override
    public void delete_item(int item_id) {
        utilityDbOperations.delete_item(item_id, GenericItemType.Expenses.getValue(), deleteCallback);
        utilityDbOperations.fetch_expenses(startDate.getTimeInMillis(), endDate.getTimeInMillis(), fetchExpensesCallback);

    }

    @Override
    public void edit_item(ExpenseItem item) {
        if (search_view.getVisibility() == View.VISIBLE) {
            toggle_search_view();
        }
        EditExpenseFragment frag = EditExpenseFragment.newInstance(item);
        frag.show(getSupportFragmentManager(), "edit_frag");
    }

    private void initializeDataStructures() {
        utilityDbOperations = Utility_DBOperations.getInstance(this);
        expense_list = new ArrayList<>();
        filtered_expenses = new ArrayList<>();
        startDate = Utility_Calendar.getLastTenYears();
        endDate = Calendar.getInstance();
        endDate.setTimeInMillis(System.currentTimeMillis());
        utilityKeyboard = Utility_Keyboard.getInstance(this);
    }

    private void inflateRCV() {
        expense_adapter = new ManageExpensesAdapter(this, filtered_expenses, this);
        rcv_expense_list.addItemDecoration(new Utility_MonthHeaderRCV(ManageExpensesActivity.this));
        rcv_expense_list.setLayoutManager(new LinearLayoutManager(this));
        rcv_expense_list.setAdapter(expense_adapter);
    }

    private void initializeUiElements() {
        search_view = findViewById(R.id.search_view);
        search_button = findViewById(R.id.search_button);
        list_title = findViewById(R.id.list_title);
        close_button = findViewById(R.id.close_button);
        rcv_expense_list = findViewById(R.id.rcv_expense_list);
        inflateRCV();
    }

    private void setupEventListeners() {

        close_button.setOnClickListener(v -> toggle_search_view());

        search_button.setOnClickListener(v -> toggle_search_view());

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.toLowerCase();
                filterList(query, filtered_expenses, expense_list);
                return true;
            }
        });
    }

    private void fetchData() {
        utilityDbOperations.fetch_expenses(startDate.getTimeInMillis(), endDate.getTimeInMillis(), fetchExpensesCallback);
    }

    public void expenseUpdated() {
        utilityDbOperations.fetch_expenses(startDate.getTimeInMillis(), endDate.getTimeInMillis(), fetchExpensesCallback);
    }

    public void toggle_search_view() {
        if (search_button.getVisibility() == View.VISIBLE) {
            search_view.setVisibility(View.VISIBLE);
            search_view.requestFocus();
            utilityKeyboard.show_keyboard(search_view);
            close_button.setVisibility(View.VISIBLE);
            search_button.setVisibility(View.GONE);
            list_title.setVisibility(View.GONE);
        } else {
            search_view.setVisibility(View.GONE);
            close_button.setVisibility(View.GONE);
            search_button.setVisibility(View.VISIBLE);
            list_title.setVisibility(View.VISIBLE);
            search_view.setQuery("", false);
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
        expense_adapter.notifyDataSetChanged();
    }

    private final Utility_DBOperations.Callback_DBOperations fetchExpensesCallback = new Utility_DBOperations.Callback_DBOperations() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSuccess(DatabaseCallbackObject databaseCallbackObject) {
            new Handler(Looper.getMainLooper()).post(() -> {
                filtered_expenses.clear();
                expense_list.clear();
                filtered_expenses.addAll(databaseCallbackObject.getDataList());
                expense_list.addAll(databaseCallbackObject.getDataList());
                expense_adapter.notifyDataSetChanged();
                rcv_expense_list.invalidateItemDecorations();
            });
        }

        @Override
        public void onFailure(Exception e) {
            new Handler(Looper.getMainLooper()).post(() -> {
                Log.e("harshTagFetchExpense", "Error : " + e.getMessage());
                Utility_UiAlerts.generateSnackBar(rcv_expense_list, "Couldn't fetch expenses", ERROR);
            });
        }

        @Override
        public void onWarning() {

        }
    };

    private final Utility_DBOperations.Callback_DBOperations deleteCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            new Handler(Looper.getMainLooper()).post(() -> Utility_UiAlerts.generateSnackBar(rcv_expense_list, "Expense deleted successfully", SUCCESS));
        }

        @Override
        public void onFailure(Exception e) {
            new Handler(Looper.getMainLooper()).post(() -> Utility_UiAlerts.generateSnackBar(rcv_expense_list, "Couldn't delete expense", ERROR));
        }

        @Override
        public void onWarning() {

        }
    };
}