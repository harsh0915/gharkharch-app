package com.itmart.gharkharch.Activities;

import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.ERROR;
import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.SUCCESS;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.Adapters.ManageExpensesAdapter;
import com.itmart.gharkharch.DataModels.DatabaseCallbackObject;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.R;
import com.itmart.gharkharch.Utilities.Utility_Calendar;
import com.itmart.gharkharch.Utilities.Utility_DBOperations;
import com.itmart.gharkharch.Utilities.Utility_MonthHeaderRCV;
import com.itmart.gharkharch.Utilities.Utility_UiAlerts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ManageExpensesActivity extends AppCompatActivity implements ManageExpensesAdapter.Click_Interface {

    private Utility_DBOperations utilityDbOperations;
    private ManageExpensesAdapter simple_adapter;
    private List<GenericItem> expense_list;
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
        fetchData();
    }

    @Override
    public void delete_item(int item_id) {
        utilityDbOperations.delete_item(item_id, GenericItemType.Expenses.getValue(), deleteCallback);
        utilityDbOperations.fetch_expenses(startDate.getTimeInMillis(), endDate.getTimeInMillis(), fetchExpensesCallback);

    }

    @Override
    public void edit_item(GenericItem item) {

    }

    private void initializeDataStructures() {
        utilityDbOperations = Utility_DBOperations.getInstance(this);
        expense_list = new ArrayList<>();
        filtered_expenses = new ArrayList<>();
        startDate = Utility_Calendar.getLastTenYears();
        endDate = Calendar.getInstance();
        endDate.setTimeInMillis(System.currentTimeMillis());
    }

    private void inflateRCV() {
        simple_adapter = new ManageExpensesAdapter(this, filtered_expenses, this);
        rcv_expense_list.addItemDecoration(new Utility_MonthHeaderRCV(ManageExpensesActivity.this));
        rcv_expense_list.setLayoutManager(new LinearLayoutManager(this));
        rcv_expense_list.setAdapter(simple_adapter);
    }

    private void initializeUiElements() {
        rcv_expense_list = findViewById(R.id.rcv_expense_list);
        inflateRCV();
    }

    private void fetchData() {
        utilityDbOperations.fetch_expenses(startDate.getTimeInMillis(), endDate.getTimeInMillis(), fetchExpensesCallback);
    }

    private final Utility_DBOperations.Callback_DBOperations fetchExpensesCallback = new Utility_DBOperations.Callback_DBOperations() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSuccess(DatabaseCallbackObject databaseCallbackObject) {
            filtered_expenses.clear();
            expense_list.clear();
            filtered_expenses.addAll(databaseCallbackObject.getDataList());
            expense_list.addAll(databaseCallbackObject.getDataList());
            simple_adapter.notifyDataSetChanged();
            rcv_expense_list.invalidateItemDecorations();
        }

        @Override
        public void onFailure(Exception e) {
            Log.e("harshTagFetchExpense", "Error : " + e.getMessage());
            Utility_UiAlerts.generateSnackBar(rcv_expense_list, "Couldn't fetch expenses", ERROR);
        }

        @Override
        public void onWarning() {

        }
    };

    private final Utility_DBOperations.Callback_DBOperations deleteCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            Utility_UiAlerts.generateSnackBar(rcv_expense_list, "Expense deleted successfully", SUCCESS);
        }

        @Override
        public void onFailure(Exception e) {
            Utility_UiAlerts.generateSnackBar(rcv_expense_list, "Couldn't delete expense", ERROR);
        }

        @Override
        public void onWarning() {

        }
    };
}