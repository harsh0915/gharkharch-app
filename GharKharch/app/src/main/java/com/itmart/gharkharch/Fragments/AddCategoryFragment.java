package com.itmart.gharkharch.Fragments;

import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.ERROR;
import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.SUCCESS;
import static com.itmart.gharkharch.Utilities.Utility_UiAlerts.WARNING;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.Adapters.SimpleListAdapter;
import com.itmart.gharkharch.DataModels.DatabaseCallbackObject;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.itmart.gharkharch.DatabaseHelper.DatabaseHelper;
import com.itmart.gharkharch.Utilities.Utility_UiAlerts;
import com.itmart.gharkharch.Utilities.Utility_DBOperations;
import com.itmart.gharkharch.Utilities.Utility_Keyboard;

public class AddCategoryFragment extends Fragment implements SimpleListAdapter.SimpleList_Interface {
    private EditText inputCategory;
    private Button addBtn;
    private List<GenericItem> category_list;
    private RecyclerView rcv_category_list;
    private SimpleListAdapter simple_adapter;
    private DatabaseHelper databaseHelper;
    private TextView page_title;
    private GenericItemType item_type;
    private Utility_DBOperations utilityDbOperations;
    private ImageView search_button;
    private SearchView search_view;
    private TextView list_title;
    private ImageView close_button;
    private HashSet<String> cat_fast_lookup;
    private Utility_Keyboard utilityKeyboard;
    private List<GenericItem> filtered_categories_list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utilityKeyboard = Utility_Keyboard.getInstance(requireContext());
        utilityDbOperations = Utility_DBOperations.getInstance(requireContext());
        item_type = GenericItemType.Category;
        category_list = new ArrayList<>();
        filtered_categories_list = new ArrayList<>();
        cat_fast_lookup = new HashSet<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_category, container, false);
        page_title = requireActivity().findViewById(R.id.page_title);
        inputCategory = view.findViewById(R.id.input_category);
        rcv_category_list = view.findViewById(R.id.rcv_category_list);
        addBtn = view.findViewById(R.id.addBtn);
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

        search_button.setOnClickListener(v -> toggle_search_view());

        close_button.setOnClickListener(v -> {
            toggle_search_view();
            search_view.setQuery("", false);
        });

        addBtn.setOnClickListener(v -> {
            utilityKeyboard.close_keyboard(v);

            String new_category = inputCategory.getText().toString();
            if (new_category.trim().isEmpty()) {
                Utility_UiAlerts.generateSnackBar(v, "A category name can't be empty!", WARNING);
            } else {
                utilityDbOperations.addCategory(new_category, addCatCallback);
                utilityDbOperations.fetch_category_list(fetchCatCallback);
                resetForm();
            }
        });

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String query = newText.toLowerCase();
                filterList(query, filtered_categories_list, category_list);
                return true;
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            page_title.setText("Expense Category");
        }
    }

    @Override
    public void delete_item(int item_id) {
        utilityDbOperations.delete_item(item_id, item_type.getValue(), deleteCallback);
    }

    @Override
    public void edit_item(GenericItem item, String editedText) {
        if (editedText.isEmpty()) {
            Utility_UiAlerts.generateSnackBar(addBtn, "An expense category can't be blank.", WARNING);
            return;
        }
        utilityDbOperations.edit_category(item.get_item_id(), editedText, editedCatCallback);
    }

    @Override
    public void database_updated() {
        utilityDbOperations.fetch_category_list(fetchCatCallback);
    }

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
        simple_adapter = new SimpleListAdapter(requireContext(), filtered_categories_list, item_type, this);
        rcv_category_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        rcv_category_list.setAdapter(simple_adapter);
    }

    public void resetForm() {
        inputCategory.setText("");
        inputCategory.setHint(R.string.hint_category);
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
                        filtered_categories_list.clear();
                        category_list.clear();
                        cat_fast_lookup.clear();
                        filtered_categories_list.addAll(databaseCallbackObject.getFilter_list());
                        category_list.addAll(databaseCallbackObject.getOriginal_list());
                        cat_fast_lookup.addAll(databaseCallbackObject.getOriginal_fast_lookup());
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
                    Utility_UiAlerts.generateSnackBar(addBtn, "Couldn't fetch categories", ERROR);
                });
            }
        }

        @Override
        public void onWarning() {

        }
    };

    private final Utility_DBOperations.Callback_DBOperations addCatCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            View view = getView();
            if (view != null) {
                view.post(() -> {
                    Utility_UiAlerts.generateSnackBar(addBtn, "Category added successfully", SUCCESS);
                });
            }
        }

        @Override
        public void onFailure(Exception e) {
            View view = getView();
            if (view != null) {
                view.post(() -> {
                    Utility_UiAlerts.generateSnackBar(addBtn, "Couldn't add category", ERROR);
                });
            }
        }

        @Override
        public void onWarning() {
            View view = getView();
            if (view != null) {
                view.post(() -> {
                    Utility_UiAlerts.generateSnackBar(addBtn, "Category already exists!!", WARNING);
                });
            }
        }
    };

    private final Utility_DBOperations.Callback_DBOperations editedCatCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(view, "Category edited successfully", SUCCESS);
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
                Utility_UiAlerts.generateSnackBar(view, "Category already exists", ERROR);
            }
        }
    };

    private final Utility_DBOperations.Callback_DBOperations deleteCallback = new Utility_DBOperations.Callback_DBOperations() {
        @Override
        public void onSuccess(DatabaseCallbackObject databaseObjectsList) {
            View view = getView();
            if (view != null) {
                Utility_UiAlerts.generateSnackBar(view, "Category deleted successfully", SUCCESS);
            }
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
                Utility_UiAlerts.generateSnackBar(view, "Couldn't delete category, its in use", WARNING);
            }
        }
    };
}
