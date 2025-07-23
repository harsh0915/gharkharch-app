package com.itmart.gharkharch.Fragments;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.itmart.gharkharch.Adapters.SearchListAdapter;
import com.itmart.gharkharch.Adapters.SimpleListAdapter;
import com.itmart.gharkharch.DataModels.Expense;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.DataModels.PaymentMode;
import com.itmart.gharkharch.DataModels.SubCategory;
import com.itmart.gharkharch.DatabaseHelper.DatabaseHelper;
import com.itmart.gharkharch.R;

import com.itmart.gharkharch.Utilities.GlobalData;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class BookExpenseFragment extends Fragment implements SearchListAdapter.ItemClicked, SimpleListAdapter.DB_Interface {

    private TextView date_picker;
    private EditText input_expense_name, input_expense_amount;
    private List<GenericItem> expense_list, sub_category_search_list;
    private HashSet<String> sub_category_fast_lookup;
    private RecyclerView rcv_expense_list, rcv_sub_category_search;
    private SimpleListAdapter simple_adapter;
    private SearchListAdapter search_adapter;
    private Button addBtn;
    private GlobalData gdata;
    private long selected_date;
    private Spinner modes_of_payment;
    private GenericItemType item_type;
    private DatabaseHelper databaseHelper;
    private int under_sub_category_id;
    private ImageView search_button;
    private SearchView search_view;
    private TextView list_title;
    private ImageView close_button;
    private Calendar startTime, endTime;
    private List<GenericItem> filtered_search_list, filtered_expenses;
    private TextView page_title;

    private enum ListType {
        Search,
        Simple
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item_type = GenericItemType.Expenses;
        gdata = GlobalData.getInstance(requireContext());
        databaseHelper = DatabaseHelper.getInstance(requireContext());
        expense_list = new ArrayList<>();
        filtered_expenses = new ArrayList<>();
        filtered_search_list = new ArrayList<>();
        sub_category_fast_lookup = new HashSet<>();
        sub_category_search_list = new ArrayList<>();
        selected_date = System.currentTimeMillis();
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR_OF_DAY, 23);
        endTime.set(Calendar.MINUTE, 59);
        endTime.set(Calendar.SECOND, 59);
        endTime.set(Calendar.MILLISECOND, 999);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_expense, container, false);
        input_expense_name = view.findViewById(R.id.input_expense_name);
        input_expense_amount = view.findViewById(R.id.input_expense_amount);
        date_picker = view.findViewById(R.id.date_picker);
        modes_of_payment = view.findViewById(R.id.modes_of_payment);
        addBtn = view.findViewById(R.id.addBtn);
        rcv_expense_list = view.findViewById(R.id.rcv_expense_list);
        rcv_sub_category_search = view.findViewById(R.id.rcv_sub_category_search_list);
        search_button = view.findViewById(R.id.search_button);
        search_view = view.findViewById(R.id.search_view);
        list_title = view.findViewById(R.id.list_title);
        close_button = view.findViewById(R.id.close_button);
        page_title = requireActivity().findViewById(R.id.page_title);
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inflate_spinner();
        inflate_expense_RCV();
        inflate_sub_cat_RCV();

        date_picker.setText(gdata.convertLongToDate(selected_date));

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
                filterList(query, filtered_search_list, sub_category_search_list, ListType.Search);
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
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select a date").setTheme(R.style.custom_date_picker).build();

            datePicker.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                selected_date = selection;
                date_picker.setText(gdata.convertLongToDate(selection));
                date_picker.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.white));
            });
        });

        search_button.setOnClickListener(v -> toggle_search_view());

        close_button.setOnClickListener(v -> {
            toggle_search_view();
            search_view.setQuery("", false);
        });

        addBtn.setOnClickListener(v -> {
            gdata.close_keyboard(v);

            String expense_name = input_expense_name.getText().toString();
            int amount = Integer.parseInt(input_expense_amount.getText().toString());
            PaymentMode selected_mode_of_payment = (PaymentMode) modes_of_payment.getSelectedItem();

            if (expense_name.isEmpty()) {
                gdata.generateSnackBar(addBtn, "An expense name can't be empty!");
                return;
            } else if (!sub_category_fast_lookup.contains(expense_name)) {
                gdata.generateSnackBar(addBtn, "Please add the \"" + expense_name + "\" heads before using it. ");
                return;
            } else
                add_expense(expense_name, under_sub_category_id, amount, selected_mode_of_payment.getValue());

            fetch_expenses();
            simple_adapter.notifyDataSetChanged();
            rcv_sub_category_search.setVisibility(View.GONE);
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
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //refresh list when coming from another frag as it may have added another data set
            fetch_expenses();
            fetch_sub_category_search_list();
            search_adapter.notifyDataSetChanged();
            simple_adapter.notifyDataSetChanged();
            page_title.setText("Book Expenses");
        }
    }

    //beginning of callbacks
    @Override
    public void database_updated(int itemId, int value) {
        gdata.delete_item(itemId, item_type.getValue(), rcv_expense_list);
        fetch_expenses();
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
        fetch_expenses();
        simple_adapter = new SimpleListAdapter(requireContext(), filtered_expenses, item_type, this);
        rcv_expense_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_expense_list.setAdapter(simple_adapter);
    }

    private void inflate_sub_cat_RCV() {
        fetch_sub_category_search_list();
        search_adapter = new SearchListAdapter(requireContext(), filtered_search_list, this);
        rcv_sub_category_search.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_sub_category_search.setAdapter(search_adapter);
    }

    public void inflate_spinner() {
        PaymentMode[] payment_modeArray = PaymentMode.values();
        //This layout is used to display the selected item when the spinner is not expanded.
        ArrayAdapter<PaymentMode> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, payment_modeArray);
        //This layout is used to display the dropdown list that appears when you click the spinner.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modes_of_payment.setAdapter(adapter);
    }

    private void add_expense(String expense_name, int under_sub_category_id, int amount, int mode_of_payment) {
        try {
            SQLiteDatabase writeable_db = databaseHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_EXPENSE_NAME, expense_name);
            values.put(DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY, under_sub_category_id);
            values.put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, amount);
            values.put(DatabaseHelper.COLUMN_EXPENSE_DATE, selected_date);
            values.put(DatabaseHelper.COLUMN_EXPENSE_MODE, mode_of_payment);

            writeable_db.insert(DatabaseHelper.TABLE_EXPENSES, null, values);

        } catch (Exception e) {
            Log.e("harshTag", "Sql error!\nError : " + e.getMessage());
        }
    }

    public void fetch_sub_category_search_list() {
        filtered_search_list.clear();
        sub_category_search_list.clear();
        sub_category_fast_lookup.clear();

        try {
            SQLiteDatabase readable_db = databaseHelper.getReadableDatabase();

            String query = "select * from " + DatabaseHelper.TABLE_SUB_CATEGORIES;
            Cursor cursor = null;

            try {
                cursor = readable_db.rawQuery(query, null);

                if (cursor.moveToFirst()) {
                    do {
                        int sub_cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUB_CATEGORY_ID));
                        String sub_cat_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME));
                        int cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UNDER_CATEGORY));

                        SubCategory subCategory = new SubCategory(sub_cat_id, sub_cat_name, cat_id);
                        sub_category_search_list.add(subCategory);
                        sub_category_fast_lookup.add(sub_cat_name);
                        filtered_search_list.add(subCategory);

                    } while (cursor.moveToNext());
                }

            } catch (Exception e) {
                Log.e("harshTagSqlQuery", " Something went wrong when querying the database. Error : " + e.getMessage());
            } finally {
                if (cursor != null) cursor.close();
            }
        } catch (Exception e) {
            Log.e("harshTagDatabaseHelper", " Something went wrong when acquiring database instance." + e.getMessage());
        }
    }

    public void fetch_expenses() {
        filtered_expenses.clear();
        expense_list.clear();
        try {
            String query = "select * from " + DatabaseHelper.TABLE_EXPENSES;
            SQLiteDatabase readable_db = databaseHelper.getReadableDatabase();
            Cursor cursor = null;

            try {
                cursor = readable_db.rawQuery(query, null);

                if (cursor.moveToFirst()) {
                    do {
                        int expense_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_ID));
                        String expense_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_NAME));
                        int sub_cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY));
                        int expense_amount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_AMOUNT));
                        long expense_date = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DATE));
                        int expense_mode = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_MODE));

                        Expense expense = new Expense(expense_id, expense_name, sub_cat_id, expense_amount, expense_date, expense_mode);
                        expense_list.add(expense);
                        filtered_expenses.add(expense);
                    } while (cursor.moveToNext());
                }

                cursor.close();
            } catch (Exception e) {
                Log.e("harshTag", "Sql error!\nError : " + e.getMessage());
            } finally {
                if (cursor != null) cursor.close();
            }
        } catch (Exception e) {
            Log.e("harshTagDatabaseHelper", " Something went wrong when acquiring database instance." + e.getMessage());
        }
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
        if (list_type == ListType.Search)
            search_adapter.notifyDataSetChanged();
        else
            simple_adapter.notifyDataSetChanged();
    }
}