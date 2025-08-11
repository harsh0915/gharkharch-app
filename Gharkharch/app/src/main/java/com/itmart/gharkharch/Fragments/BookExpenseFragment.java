package com.itmart.gharkharch.Fragments;

import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.DEFAULT;
import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.ERROR;
import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.SUCCESS;
import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.WARNING;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.itmart.gharkharch.Adapters.SearchListAdapter;
import com.itmart.gharkharch.Adapters.SimpleListAdapter;
import com.itmart.gharkharch.DataModels.DatabaseCallbackObject;
import com.itmart.gharkharch.DataModels.ExpenseItem;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.DataModels.PaymentMode;
import com.itmart.gharkharch.DatabaseHelper.DatabaseHelper;
import com.itmart.gharkharch.R;

import com.itmart.gharkharch.Utilities.Utility_Calendar;
import com.itmart.gharkharch.Utilities.Utility_UiAlerts;
import com.itmart.gharkharch.Utilities.Utility_DBOperations;
import com.itmart.gharkharch.Utilities.Utility_DateFormats;
import com.itmart.gharkharch.Utilities.Utility_Keyboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class BookExpenseFragment extends Fragment implements SearchListAdapter.ItemClicked, SimpleListAdapter.SimpleList_Interface {

    private final String CASH = "CASH";
    private final String UPI = "UPI";
    private final String CARD = "CARD";
    private TextView date_picker;
    private EditText input_expense_name;
    private EditText input_expense_amount;
    private EditText input_expense_remarks;
    private List<GenericItem> expense_list;
    private List<GenericItem> sub_category_search_list;
    private HashSet<String> sub_category_fast_lookup;
    private RecyclerView rcv_expense_list;
    private RecyclerView rcv_sub_category_search;
    private SimpleListAdapter simple_adapter;
    private SearchListAdapter search_adapter;
    private Button addBtn;
    private Button cancelBtn;
    private Utility_DBOperations utilityDbOperations;
    private long selected_date;
    private TextView modes_of_payment;
    private GenericItemType item_type;
    private int under_sub_category_id;
    private ImageView search_button;
    private SearchView search_view;
    private TextView list_title;
    private ImageView close_button;
    private List<GenericItem> filtered_sub_cat_search_list;
    private List<GenericItem> filtered_expenses;
    private TextView page_title;
    private boolean isEditMode;
    private PaymentMode selected_mode_of_payment;
    private Utility_Keyboard utilityKeyboard;
    private int expenseId;
    private static final int INVALID_EXPENSE_ID = -1;
    private ImageView showCalendar;
    private Calendar startDate;
    private Calendar endDate;

    private enum ListType {
        Search, Simple
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item_type = GenericItemType.Expenses;
        utilityDbOperations = Utility_DBOperations.getInstance(requireContext());
        utilityKeyboard = Utility_Keyboard.getInstance(requireContext());
        expense_list = new ArrayList<>();
        filtered_expenses = new ArrayList<>();
        filtered_sub_cat_search_list = new ArrayList<>();
        sub_category_fast_lookup = new HashSet<>();
        sub_category_search_list = new ArrayList<>();
        selected_date = System.currentTimeMillis();
        selected_mode_of_payment = PaymentMode.CASH;
        startDate = Utility_Calendar.getStartOfMonth(System.currentTimeMillis());
        endDate = Utility_Calendar.getEndOfMonth(System.currentTimeMillis());
        expenseId = -1;
        isEditMode = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_expense, container, false);
        input_expense_name = view.findViewById(R.id.input_expense_name);
        input_expense_amount = view.findViewById(R.id.input_expense_amount);
        input_expense_remarks = view.findViewById(R.id.input_expense_remarks);
        date_picker = view.findViewById(R.id.date_picker);
        modes_of_payment = view.findViewById(R.id.modes_of_payment);
        addBtn = view.findViewById(R.id.addBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
        rcv_expense_list = view.findViewById(R.id.rcv_expense_list);
        rcv_sub_category_search = view.findViewById(R.id.rcv_sub_category_search_list);
        search_button = view.findViewById(R.id.search_button);
        search_view = view.findViewById(R.id.search_view);
        list_title = view.findViewById(R.id.list_title);
        close_button = view.findViewById(R.id.close_button);
        showCalendar = view.findViewById(R.id.show_calendar);
        page_title = requireActivity().findViewById(R.id.page_title);
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inflate_expense_RCV();
        inflate_sub_cat_RCV();

        date_picker.setText(Utility_DateFormats.convertLongToDate_DD_MM_YYYY(selected_date));

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (input_expense_name.hasFocus()) input_expense_name.clearFocus();
                else {
                    setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });

        input_expense_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rcv_sub_category_search.setVisibility(View.VISIBLE);
                String query = s.toString().toLowerCase();
                filterList(query, filtered_sub_cat_search_list, sub_category_search_list, ListType.Search);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        input_expense_name.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                rcv_sub_category_search.setVisibility(View.VISIBLE);
            } else {
                rcv_sub_category_search.setVisibility(View.GONE);
            }
        });

        date_picker.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select a date").setSelection(selected_date).setTheme(R.style.custom_date_picker).build();

            datePicker.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                selected_date = selection;
                date_picker.setText(Utility_DateFormats.convertLongToDate_DD_MM_YYYY(selection));
            });
        });

        search_button.setOnClickListener(v -> toggle_search_view());

        close_button.setOnClickListener(v -> {
            toggle_search_view();
            search_view.setQuery("", false);
        });

        addBtn.setOnClickListener(v -> {
            utilityKeyboard.close_keyboard(v);

            String expense_name = input_expense_name.getText().toString();
            String amountString = input_expense_amount.getText().toString();
            String expense_remark = input_expense_remarks.getText().toString().trim();

            //empty expense name
            if (expense_name.isEmpty()) {
                Utility_UiAlerts.generateSnackBar(addBtn, "An expense name can't be empty!", WARNING);
                return;
            }
            //trying to use a category without defining it first
            if (!sub_category_fast_lookup.contains(expense_name)) {
                Utility_UiAlerts.generateSnackBar(addBtn, "Please add the \"" + expense_name + "\" heads before using it. ", WARNING);
                return;
            }
            //amount can't be empty
            if (amountString.isEmpty()) {
                Utility_UiAlerts.generateSnackBar(addBtn, "Amount cannot be empty", WARNING);
                return;
            }

            if (isEditMode) {
                if (expenseId == INVALID_EXPENSE_ID) {
                    Utility_UiAlerts.generateSnackBar(rcv_expense_list, "Invalid expense", ERROR);
                    resetForm();
                    return;
                }
                utilityDbOperations.edit_expense(expenseId, under_sub_category_id, Integer.parseInt(amountString), selected_date, selected_mode_of_payment.getValue(), expense_remark, editExpenseCallback);
            } else {
                utilityDbOperations.add_expense(under_sub_category_id, Integer.parseInt(amountString), selected_date, selected_mode_of_payment.getValue(), expense_remark, bookExpenseCallback);
            }

            utilityDbOperations.fetch_expenses(startDate.getTimeInMillis(), endDate.getTimeInMillis(), fetchExpensesCallback);
            resetForm();
        });

        cancelBtn.setOnClickListener(v -> {
            resetForm();
        });

        modes_of_payment.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View dialog_layout = LayoutInflater.from(requireContext()).inflate(R.layout.dilaog_modes_of_payment, null);
            builder.setView(dialog_layout);

            Dialog dialog = builder.create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            TextView cash, upi, card;

            cash = dialog_layout.findViewById(R.id.modeCash);
            upi = dialog_layout.findViewById(R.id.modeUpi);
            card = dialog_layout.findViewById(R.id.modeCard);

            cash.setOnClickListener(item -> {
                selected_mode_of_payment = PaymentMode.CASH;
                modes_of_payment.setText(CASH);
                dialog.dismiss();
            });

            upi.setOnClickListener(item -> {
                selected_mode_of_payment = PaymentMode.UPI;
                modes_of_payment.setText(UPI);
                dialog.dismiss();
            });

            card.setOnClickListener(item -> {
                selected_mode_of_payment = PaymentMode.CARD;
                modes_of_payment.setText(CARD);
                dialog.dismiss();
            });

            dialog.show();

        });

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.toLowerCase();
                filterList(query, filtered_expenses, expense_list, ListType.Simple);
                return true;
            }
        });

        showCalendar.setOnClickListener(v -> {
            MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select a date range")
                    .setSelection(new Pair<>(startDate.getTimeInMillis(), endDate.getTimeInMillis()))
                    .setTheme(R.style.custom_date_picker)
                    .build();

            datePicker.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "MATERIAL_DATE_RANGE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                startDate.setTimeInMillis(selection.first);
                endDate.setTimeInMillis(selection.second);
                utilityDbOperations.fetch_expenses(startDate.getTimeInMillis(), endDate.getTimeInMillis(), fetchExpensesCallback);
            });
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //refresh list when coming from another frag as it may have added another data set
            utilityDbOperations.fetch_sub_category_list(fetchSubCatCallback);
            utilityDbOperations.fetch_expenses(startDate.getTimeInMillis(), endDate.getTimeInMillis(), fetchExpensesCallback);
            page_title.setText("Book Expenses");
        } else {
            resetForm();
        }
    }

    @Override
    public void delete_item(int item_id) {
        utilityDbOperations.delete_item(item_id, item_type.getValue(), deleteCallback);
    }

    //beginning of callbacks
    @Override
    public void edit_item(GenericItem item) {
        ExpenseItem expense = (ExpenseItem) item;
        Utility_UiAlerts.generateSnackBar(rcv_expense_list, "Editing the expense", DEFAULT);
        isEditMode = true;
        cancelBtn.setVisibility(View.VISIBLE);
        addBtn.setText("Update");
        input_expense_name.setText(String.valueOf(expense.get_item_name()));
        input_expense_amount.setText(String.valueOf(expense.get_item_amount()));
        input_expense_remarks.setText(expense.get_remarks() == null ? "" : expense.get_remarks());
        selected_mode_of_payment = PaymentMode.fromValue(expense.get_mode_of_payment());
        setModeOfPayment();
        under_sub_category_id = expense.get_under_id();
        selected_date = expense.get_date();
        date_picker.setText(Utility_DateFormats.convertLongToDate_DD_MM_YYYY(selected_date));
        expenseId = expense.get_item_id();
        rcv_sub_category_search.setVisibility(View.GONE);
    }

    @Override
    public void database_updated() {
        utilityDbOperations.fetch_expenses(startDate.getTimeInMillis(), endDate.getTimeInMillis(), fetchExpensesCallback);
    }

    @Override
    public void on_item_clicked(GenericItem clicked_item) {
        input_expense_name.setText(clicked_item.get_item_name());
        under_sub_category_id = clicked_item.get_item_id();
        //this line moves the cursor to the end of the text inside the edi text
        input_expense_name.setSelection(input_expense_name.getText().length());
        rcv_sub_category_search.setVisibility(View.GONE);
    }
    //end of callbacks

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

    private void inflate_expense_RCV() {
        utilityDbOperations.fetch_expenses(startDate.getTimeInMillis(), endDate.getTimeInMillis(), fetchExpensesCallback);
        simple_adapter = new SimpleListAdapter(requireContext(), filtered_expenses, item_type, this);
        rcv_expense_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_expense_list.setAdapter(simple_adapter);
    }

    private void inflate_sub_cat_RCV() {
        utilityDbOperations.fetch_sub_category_list(fetchSubCatCallback);
        search_adapter = new SearchListAdapter(requireContext(), filtered_sub_cat_search_list, this);
        rcv_sub_category_search.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_sub_category_search.setAdapter(search_adapter);
    }

    public void setModeOfPayment() {
        switch (selected_mode_of_payment.getValue()) {
            case 1:
                modes_of_payment.setText(CASH);
                break;
            case 2:
                modes_of_payment.setText(UPI);
                break;
            case 3:
                modes_of_payment.setText(CARD);
                break;
            default:
                break;
        }
    }

    public void resetForm() {
        addBtn.setText("Save");
        input_expense_amount.setText("");
        input_expense_name.setText("");
        input_expense_remarks.setText("");
        modes_of_payment.setText(CASH);
        cancelBtn.setVisibility(View.GONE);
        rcv_sub_category_search.setVisibility(View.GONE);
        startDate = Utility_Calendar.getStartOfMonth(System.currentTimeMillis());
        endDate = Utility_Calendar.getEndOfMonth(System.currentTimeMillis());
        if (isEditMode) {
            selected_date = System.currentTimeMillis();
            date_picker.setText(Utility_DateFormats.convertLongToDate_DD_MM_YYYY(selected_date));
        }
        if(search_button.getVisibility()==View.GONE){
            toggle_search_view();
            search_view.setQuery("", false);
        }
        isEditMode = false;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterList(String query, List<GenericItem> list_toBeFiltered, List<GenericItem> filterFrom, ListType list_type) {
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
        if (list_type == ListType.Search) search_adapter.notifyDataSetChanged();
        else simple_adapter.notifyDataSetChanged();
    }

    private final Utility_DBOperations.Callback_DBOperations fetchSubCatCallback = new Utility_DBOperations.Callback_DBOperations() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSuccess(DatabaseCallbackObject databaseCallbackObject) {
            View view = getView();
            if (view != null) {
                view.post(() -> {
                    if (isAdded() && !isHidden() && view.getWindowToken() != null) {
                        filtered_sub_cat_search_list.clear();
                        sub_category_search_list.clear();
                        sub_category_fast_lookup.clear();
                        filtered_sub_cat_search_list.addAll(databaseCallbackObject.getDataList());
                        sub_category_search_list.addAll(databaseCallbackObject.getDataList());
                        sub_category_fast_lookup.addAll(databaseCallbackObject.getDataFastLookup());
                        search_adapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                view.post(() -> {
                    View fragmentView = getView();
                    if (isAdded() && !isHidden() && fragmentView.getWindowToken() != null) {
                        Log.e("harshTagFetchSubCat", "Error : " + e.getMessage());
                        Utility_UiAlerts.generateSnackBar(fragmentView, "Couldn't fetch expense heads", ERROR);
                    }
                });
            }
        }

        @Override
        public void onWarning() {

        }
    };

    private final Utility_DBOperations.Callback_DBOperations fetchExpensesCallback = new Utility_DBOperations.Callback_DBOperations() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSuccess(DatabaseCallbackObject databaseCallbackObject) {
            View view = getView();
            if (view != null) {
                view.post(() -> {
                    if (isAdded() && !isHidden() && view.getWindowToken() != null) {
                        filtered_expenses.clear();
                        expense_list.clear();
                        filtered_expenses.addAll(databaseCallbackObject.getDataList());
                        expense_list.addAll(databaseCallbackObject.getDataList());
                        simple_adapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                view.post(() -> {
                    View fragmentView = getView();
                    if (isAdded() && !isHidden() && fragmentView.getWindowToken() != null) {
                        Log.e("harshTagFetchExpense", "Error : " + e.getMessage());
                        Utility_UiAlerts.generateSnackBar(fragmentView, "Couldn't fetch expenses", ERROR);
                    }
                });
            }
        }

        @Override
        public void onWarning() {

        }
    };

    private final Utility_DBOperations.Callback_DBOperations bookExpenseCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(view, "Expense booked successfully", SUCCESS);
            }
            rcv_sub_category_search.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                Log.e("harshTagBookExpense", "Error : " + e.getMessage());
                Utility_UiAlerts.generateSnackBar(view, "Couldn't book expense", ERROR);
            }
            rcv_sub_category_search.setVisibility(View.GONE);
        }

        @Override
        public void onWarning() {
            rcv_sub_category_search.setVisibility(View.GONE);
        }
    };

    private final Utility_DBOperations.Callback_DBOperations editExpenseCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(view, "Expense edited successfully", SUCCESS);
            }
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                Log.e("harshTagDeleteExpense", "Error : " + e.getMessage());
                Utility_UiAlerts.generateSnackBar(view, "Couldn't edit expense", ERROR);
            }
        }

        @Override
        public void onWarning() {

        }
    };

    private final Utility_DBOperations.Callback_DBOperations deleteCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(view, "Expense deleted successfully", SUCCESS);
            }
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                Log.e("harshTagDeleteExpense", "Error : " + e.getMessage());
                Utility_UiAlerts.generateSnackBar(view, "Couldn't delete expense", ERROR);
            }
        }

        @Override
        public void onWarning() {
        }
    };
}