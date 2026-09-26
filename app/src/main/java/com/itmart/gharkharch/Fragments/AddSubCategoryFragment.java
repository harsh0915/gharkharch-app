package com.itmart.gharkharch.Fragments;

import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.DEFAULT;
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
import com.itmart.gharkharch.DataModels.SubCatItem;
import com.itmart.gharkharch.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.itmart.gharkharch.Adapters.SearchListAdapter;

import com.itmart.gharkharch.Utilities.Utility_UiAlerts;
import com.itmart.gharkharch.Utilities.Utility_DBOperations;
import com.itmart.gharkharch.Utilities.Utility_Keyboard;


public class AddSubCategoryFragment extends Fragment implements SearchListAdapter.ItemClicked, SimpleListAdapter.SimpleList_Interface {

    private final int INVALID_ID = -1;
    private Button addBtn;
    private Button cancelBtn;
    private EditText inputSubCategory;
    private EditText inputCategory;
    private List<GenericItem> category_search_list;
    private List<GenericItem> sub_category_list;
    private HashSet<String> category_fast_lookup;
    private RecyclerView rcv_category_search;
    private SearchListAdapter search_adapter;
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
    private boolean isEditMode;
    private int under_catId;
    private int subCatId;

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
        filtered_cat_search_list = new ArrayList<>();
        sub_category_list = new ArrayList<>();
        category_search_list = new ArrayList<>();
        category_fast_lookup = new HashSet<>();
        filtered_sub_categories = new ArrayList<>();
        under_catId = -1;
        subCatId = -1;
        isEditMode = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sub_category, container, false);
        page_title = requireActivity().findViewById(R.id.page_title);
        inputSubCategory = view.findViewById(R.id.input_sub_category);
        inputCategory = view.findViewById(R.id.input_category);
        addBtn = view.findViewById(R.id.addBtn);
        cancelBtn = view.findViewById(R.id.cancelBtn);
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

        inputCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0)
                    rcv_category_search.setVisibility(View.GONE);
                else
                    rcv_category_search.setVisibility(View.VISIBLE);
                String query = s.toString().toLowerCase();
                filterList(query, filtered_cat_search_list, category_search_list, ListType.Search);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputCategory.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && inputCategory.getText().toString().length() > 1) {
                rcv_category_search.setVisibility(View.VISIBLE);
            } else {
                rcv_category_search.setVisibility(View.GONE);
            }
        });

        search_button.setOnClickListener(v -> toggle_search_view());

        close_button.setOnClickListener(v -> toggle_search_view());

        addBtn.setOnClickListener(v -> {
            utilityKeyboard.close_keyboard(v);

            String sub_cat_name = inputSubCategory.getText().toString(), cat_name = inputCategory.getText().toString();
            if (sub_cat_name.isEmpty()) {
                Utility_UiAlerts.generateSnackBar(addBtn, "An expense head name can't be empty!", WARNING);
                return;
            }
            if (cat_name.isEmpty()) {
                Utility_UiAlerts.generateSnackBar(addBtn, "An expense head can't be added without a category!", WARNING);
                return;
            }
            if (!category_fast_lookup.contains(cat_name)) {
                Utility_UiAlerts.generateSnackBar(addBtn, "No such category found!", WARNING);
                return;
            }
            if (isEditMode) {
                if (subCatId == INVALID_ID) {
                    Utility_UiAlerts.generateSnackBar(rcv_sub_category_list, "Invalid expense", ERROR);
                    resetForm();
                    return;
                }
                utilityDbOperations.edit_sub_category(subCatId, under_catId, sub_cat_name, editedSubCatCallback);
            } else {
                if (under_catId == INVALID_ID) {
                    Utility_UiAlerts.generateSnackBar(rcv_sub_category_list, "Please select a category from the list!", WARNING);
                    return;
                }
                utilityDbOperations.addSubCategory(sub_cat_name, under_catId, addSubCatCallback);
            }

            utilityDbOperations.fetch_sub_category_list(fetchSubCatCallback);
            resetForm();
        });

        cancelBtn.setOnClickListener(v -> resetForm());

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

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //refresh list when coming from another frag as it may have added another data set
            utilityDbOperations.fetch_category_list(fetchCatCallback);
            page_title.setText("Expense Heads");
        } else {
            resetForm();
        }
    }

    @Override
    public void delete_item(int item_id) {
        utilityDbOperations.delete_item(item_id, item_type.getValue(), deleteCallback);
    }

    //beginning of callbacks
    @SuppressLint("SetTextI18n")
    @Override
    public void edit_item(GenericItem item) {
        SubCatItem subCatItem = (SubCatItem) item;
        Utility_UiAlerts.generateSnackBar(rcv_sub_category_list, "Editing the expense head", DEFAULT);
        isEditMode = true;
        cancelBtn.setVisibility(View.VISIBLE);
        addBtn.setText("Update");
        subCatId = subCatItem.get_item_id();
        under_catId = subCatItem.get_under_id();
        inputSubCategory.setText(subCatItem.get_item_name());
        inputCategory.setText(subCatItem.get_under_cat_name());
        rcv_category_search.setVisibility(View.GONE);
        if (search_view.getVisibility() == View.VISIBLE) {
            toggle_search_view();
        }
    }

    @Override
    public void database_updated() {
        utilityDbOperations.fetch_sub_category_list(fetchSubCatCallback);
    }

    @Override
    public void on_item_clicked(GenericItem clicked_item) {
        inputCategory.setText(clicked_item.get_item_name());
        under_catId = clicked_item.get_item_id();
        //this line moves the cursor to the end of the text inside the edi text
        inputCategory.setSelection(inputCategory.getText().length());
        rcv_category_search.setVisibility(View.GONE);
    }
    //ending of callbacks

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

    @SuppressLint("SetTextI18n")
    public void resetForm() {
        isEditMode = false;
        inputCategory.setText("");
        addBtn.setText("Save");
        cancelBtn.setVisibility(View.GONE);
        inputSubCategory.setText("");
        rcv_category_search.setVisibility(View.GONE);
        inputCategory.setHint(R.string.hint_category);
        inputSubCategory.setHint(R.string.hint_sub_category);
        if (search_view.getVisibility() == View.VISIBLE) {
            toggle_search_view();
        }
        inputSubCategory.requestFocus();
        under_catId = -1;
        subCatId = -1;
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
                view.post(() -> Utility_UiAlerts.generateSnackBar(addBtn, "Expense head added successfully", SUCCESS));
            }
            rcv_category_search.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                view.post(() -> {
                    Log.e("harshTagBookExpense", "Error : " + e.getMessage());
                    Utility_UiAlerts.generateSnackBar(view, "Couldn't add expense head", ERROR);
                });
            }
            rcv_category_search.setVisibility(View.GONE);
        }

        @Override
        public void onWarning() {
            View view = getView();
            if (view != null) {
                view.post(() -> Utility_UiAlerts.generateSnackBar(view, "Expense head already exists!!", WARNING));
            }
            rcv_category_search.setVisibility(View.GONE);
        }
    };

    private final Utility_DBOperations.Callback_DBOperations editedSubCatCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            View view = getView();
            if (view != null) {
                view.post(() -> Utility_UiAlerts.generateSnackBar(view, "Expense head edited successfully", SUCCESS));
            }
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                view.post(() -> Utility_UiAlerts.generateSnackBar(view, "Couldn't edit category", ERROR));
            }
        }

        @Override
        public void onWarning() {
            View view = getView();
            if (view != null) {
                view.post(() -> Utility_UiAlerts.generateSnackBar(view, "Expense head already exists", ERROR));
            }
        }
    };

    private final Utility_DBOperations.Callback_DBOperations deleteCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            View view = getView();
            if (view != null) {
                view.post(() -> Utility_UiAlerts.generateSnackBar(view, "Expense Head deleted successfully", SUCCESS));
            }
            rcv_category_search.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                view.post(() -> {
                    Log.e("harshTagDeleteExpense", "Error : " + e.getMessage());
                    Utility_UiAlerts.generateSnackBar(view, "Couldn't delete expense head", ERROR);
                });
            }
        }

        @Override
        public void onWarning() {
            View view = getView();
            if (view != null) {
                view.post(() -> Utility_UiAlerts.generateSnackBar(view, "Sorry, already in use", WARNING));
            }
        }
    };
}