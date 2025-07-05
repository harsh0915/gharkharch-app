package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.DataModels.Category;
import com.itmart.gharkharch.R;

import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    private Context context;
    private final List<SearchableItem> item_list;
    private List<String> backup_list;
    private final ItemClicked interface_ItemClicked;

    public interface ItemClicked {
        public void on_item_clicked(SearchableItem clicked_item);
    }

    public interface SearchableItem {
        int get_item_id();

        String get_item_name();
    }

    public SearchListAdapter(Context context, List<SearchableItem> item_list, ItemClicked interface_ItemClicked) {
        this.context = context;
        this.item_list = item_list;
        this.interface_ItemClicked = interface_ItemClicked;
    }

    @NonNull
    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListAdapter.ViewHolder holder, int position) {
        SearchableItem item = item_list.get(position);
        holder.item_name.setText(item.get_item_name());
        holder.itemView.setOnClickListener(v -> {
            interface_ItemClicked.on_item_clicked(item);
        });
    }

    @Override
    public int getItemCount() {
        return item_list.size();
    }

    public void update_list(List<SearchableItem> updated_list) {
        item_list.clear();
        item_list.addAll(updated_list);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
        }
    }
}
