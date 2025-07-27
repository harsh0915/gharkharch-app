package com.itmart.gharkharch.Fragments;

import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.ERROR;
import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.SUCCESS;
import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.WARNING;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.itmart.gharkharch.Adapters.SimpleListAdapter;
import com.itmart.gharkharch.DataModels.DatabaseCallbackObject;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.itmart.gharkharch.Adapters.SearchListAdapter;

import com.itmart.gharkharch.DatabaseHelper.DatabaseHelper;
import com.itmart.gharkharch.Utilities.Utility_UiAlerts;
import com.itmart.gharkharch.Utilities.Utility_DBOperations;
import com.itmart.gharkharch.Utilities.Utility_Keyboard;


public class AddSubCategoryFragment extends Fragment implements SearchListAdapter.ItemClicked, SimpleListAdapter.SimpleList_Interface {
    private Button addBtn;
    private EditText inputSubCategory;
    private EditText inputCategory;
    private List<GenericItem> category_search_list;
    private List<GenericItem> sub_category_list;
    private HashSet<String> category_fast_lookup;
    private RecyclerView rcv_category_search;
    private SearchListAdapter search_adapter;
    private DatabaseHelper databaseHelper;
    private int under_category_id;
    private TextView page_title;
    private SimpleListAdapter simple_adapter;
    private RecyclerView rcv_sub_category_list;
    private GenericItemType item_type;
    private Utility_DBOperations utilityDbOperations;
    private ImageView search_button;
    private SearchView search_view;
    private TextView list_title;
    private ImageView close_button;
    private Utility_Keyboard utilityKeyboard;
    private List<GenericItem> filtered_cat_search_list;
    private List<GenericItem> filtered_sub_categories;

    private enum ListType {
        Search,
        Simple
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item_type = GenericItemType.Sub_Category;
        utilityDbOperations = Utility_DBOperations.getInstance(requireContext());
        utilityKeyboard = Utility_Keyboard.getInstance(requireContext());
        databaseHelper = DatabaseHelper.getInstance(requireContext());
        filtered_cat_search_list = new ArrayList<>();
        sub_category_list = new ArrayList<>();
        category_search_list = new ArrayList<>();
        category_fast_lookup = new HashSet<>();
        filtered_sub_categories = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sub_category, container, false);
        page_title = requireActivity().findViewById(R.id.page_title);
        inputSubCategory = view.findViewById(R.id.input_sub_category);
        inputCategory = view.findViewById(R.id.input_category);
        addBtn = view.findViewById(R.id.addBtn);
        rcv_category_search = view.findViewById(R.id.category_search_list);
        rcv_sub_category_list = view.findViewById(R.id.rcv_sub_category_list);
        search_button = view.findViewById(R.id.search_button);
        search_view = view.findViewById(R.id.search_view);
        list_title = view.findViewById(R.id.list_title);
        close_button = view.findViewById(R.id.close_button);
        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inflate_category_RCV();
        inflate_sub_category_RCV();

        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (inputCategory.hasFocus()) inputCategory.clearFocus();
                else {
                    setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });

        //TODO:bind the search list adapter with the input text
        inputCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rcv_category_search.setVisibility(View.VISIBLE);
                String query = s.toString().toLowerCase();
                filterList(query, filtered_cat_search_list, category_search_list, ListType.Search);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                rcv_category_search.setVisibility(View.VISIBLE);
            } else {
                rcv_category_search.setVisibility(View.GONE);
            }
        });

        search_button.setOnClickListener(v -> toggle_search_view());

        close_button.setOnClickListener(v -> {
            toggle_search_view();
            search_view.setQuery("", false);
        });

        addBtn.setOnClickListener(v -> {
            utilityKeyboard.close_keyboard(v);

            String sub_cat_name = inputSubCategory.getText().toString(), cat_name = inputCategory.getText().toString();
            if (sub_cat_name.isEmpty()) {
                Utility_UiAlerts.generateSnackBar(addBtn, "An expense head name can't be empty!", WARNING);
                return;
            } else if (cat_name.isEmpty()) {
                Utility_UiAlerts.generateSnackBar(addBtn, "An expense head can't be added without a category!", WARNING);
                return;
            } else if (!category_fast_lookup.contains(cat_name)) {
                Utility_UiAlerts.generateSnackBar(addBtn, "Please add the \"" + cat_name + "\" category before using it. ", WARNING);
                return;
            } else {
                utilityDbOperations.addSubCategory(sub_cat_name, under_category_id, addSubCatCallback);
            }

            utilityDbOperations.fetch_sub_category_list(fetchSubCatCallback);
            resetForm();
        });

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.toLowerCase();
                filterList(query, filtered_sub_categories, sub_category_list, ListType.Simple);
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
            utilityDbOperations.fetch_category_list(fetchCatCallback);
            page_title.setText("Expense Heads");
        }
    }

    @Override
    public void delete_item(int item_id) {
        utilityDbOperations.delete_item(item_id, item_type.getValue(), deleteCallback);
    }

    //beginning of callbacks
    @Override
    public void edit_item(GenericItem item, String editedText) {
        if (editedText.isEmpty()) {
            Utility_UiAlerts.generateSnackBar(addBtn, "An expense head can't be blank.", WARNING);
            return;
        }
        utilityDbOperations.edit_sub_category(item.get_item_id(), editedText, editedSubCatCallback);
    }

    @Override
    public void database_updated() {
        utilityDbOperations.fetch_sub_category_list(fetchSubCatCallback);
    }

    @Override
    public void on_item_clicked(GenericItem clicked_item) {
        inputCategory.setText(clicked_item.get_item_name());
        under_category_id = clicked_item.get_item_id();
        //this line moves the cursor to the end of the text inside the edi text
        inputCategory.setSelection(inputCategory.getText().length());
        rcv_category_search.setVisibility(View.GONE);
    }
    //ending of callbacks

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

    private void inflate_category_RCV() {
        utilityDbOperations.fetch_category_list(fetchCatCallback);
        search_adapter = new SearchListAdapter(requireContext(), filtered_cat_search_list, this);
        rcv_category_search.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_category_search.setAdapter(search_adapter);
    }

    private void inflate_sub_category_RCV() {
        utilityDbOperations.fetch_sub_category_list(fetchSubCatCallback);
        simple_adapter = new SimpleListAdapter(requireContext(), filtered_sub_categories, item_type, this);
        rcv_sub_category_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_sub_category_list.setAdapter(simple_adapter);
    }

    public void resetForm() {
        inputCategory.setText("");
        inputSubCategory.setText("");
        rcv_category_search.setVisibility(View.GONE);
        inputCategory.setHint(R.string.hint_category);
        inputSubCategory.setHint(R.string.hint_sub_category);
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

    private final Utility_DBOperations.Callback_DBOperations fetchCatCallback = new Utility_DBOperations.Callback_DBOperations() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSuccess(DatabaseCallbackObject databaseCallbackObject) {
            View view = getView();
            if (view != null) {
                view.post(() -> {
                    if (isAdded() && !isHidden() && view.getWindowToken() != null) {
                        filtered_cat_search_list.clear();
                        category_search_list.clear();
                        category_fast_lookup.clear();
                        filtered_cat_search_list.addAll(databaseCallbackObject.getDataList());
                        category_search_list.addAll(databaseCallbackObject.getDataList());
                        category_fast_lookup.addAll(databaseCallbackObject.getDataFastLookup());
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
                    Log.e("harshTagFetchExpense", "Error : " + e.getMessage());
                    Utility_UiAlerts.generateSnackBar(view, "Couldn't fetch expense", ERROR);
                });
            }
        }

        @Override
        public void onWarning() {

        }
    };

    private final Utility_DBOperations.Callback_DBOperations fetchSubCatCallback = new Utility_DBOperations.Callback_DBOperations() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onSuccess(DatabaseCallbackObject databaseCallbackObject) {
            View view = getView();
            if (view != null) {
                view.post(() -> {
                    if (isAdded() && !isHidden() && view.getWindowToken() != null) {
                        filtered_sub_categories.clear();
                        sub_category_list.clear();
                        filtered_sub_categories.addAll(databaseCallbackObject.getDataList());
                        sub_category_list.addAll(databaseCallbackObject.getDataList());
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

    private final Utility_DBOperations.Callback_DBOperations addSubCatCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(addBtn, "Expense head added successfully", SUCCESS);
            }
            rcv_category_search.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                Log.e("harshTagBookExpense", "Error : " + e.getMessage());
                Utility_UiAlerts.generateSnackBar(view, "Couldn't add expense head", ERROR);
            }
            rcv_category_search.setVisibility(View.GONE);
        }

        @Override
        public void onWarning() {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(view, "Expense head already exists!!", WARNING);
            }
            rcv_category_search.setVisibility(View.GONE);
        }
    };

    private final Utility_DBOperations.Callback_DBOperations editedSubCatCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(view, "Expense head edited successfully", SUCCESS);
            }
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(view, "Couldn't edit category", ERROR);
            }
        }

        @Override
        public void onWarning() {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(view, "Expense head already exists", ERROR);
            }
        }
    };

    private final Utility_DBOperations.Callback_DBOperations deleteCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(view, "Expense Head deleted successfully", SUCCESS);
            }
            rcv_category_search.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                Log.e("harshTagDeleteExpense", "Error : " + e.getMessage());
                Utility_UiAlerts.generateSnackBar(view, "Couldn't delete expense head", ERROR);
            }
        }

        @Override
        public void onWarning() {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(view, "Sorry, already in use", WARNING);
            }
        }
    };
}