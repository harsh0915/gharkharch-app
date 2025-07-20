package com.itmart.gharkharch.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.R;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    private final List<GenericItem> search_item_list, backup_list;
    private final ItemClicked interface_ItemClicked;

    public interface ItemClicked {
        public void on_item_clicked(GenericItem clicked_item);
    }

    public SearchListAdapter(Context context, List<GenericItem> search_item_list, ItemClicked interface_ItemClicked) {
        this.search_item_list = search_item_list;
        this.interface_ItemClicked = interface_ItemClicked;
        this.backup_list = new ArrayList<>();
    }

    @NonNull
    @Override
    public SearchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchListAdapter.ViewHolder holder, int position) {
        GenericItem item = search_item_list.get(position);
        holder.item_name.setText(item.get_item_name());
        holder.itemView.setOnClickListener(v -> {
            interface_ItemClicked.on_item_clicked(item);
        });
    }

    @Override
    public int getItemCount() {
        return search_item_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
        }
    }
}
