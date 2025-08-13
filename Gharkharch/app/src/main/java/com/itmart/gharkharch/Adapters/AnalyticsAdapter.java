package com.itmart.gharkharch.Adapters;

import static com.itmart.gharkharch.DataModels.SortBy.Date;
import static com.itmart.gharkharch.DataModels.SortBy.Month;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.DataModels.ExpenseItem;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.ItemForAnalysis;
import com.itmart.gharkharch.R;

import java.util.List;

public class AnalyticsAdapter extends RecyclerView.Adapter<AnalyticsAdapter.ViewHolder> {

    private final Context context;
    private final List<GenericItem> dataList;
    private String[] xAxisLabels;

    public AnalyticsAdapter(Context context, List<GenericItem> dataList) {
        this.context = context;
        this.dataList = dataList;
        getXAxisLabelsFromDataList();
    }

    @NonNull
    @Override
    public AnalyticsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analytics, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalyticsAdapter.ViewHolder holder, int position) {
        ExpenseItem item = (ExpenseItem) dataList.get(position);
        holder.item_name.setText("expenese");
        holder.item_amount.setText("\u20B9" + item.get_item_amount());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void getXAxisLabelsFromDataList() {
        xAxisLabels = dataList.stream().map(item -> item != null ? item.get_item_name() : "").toArray(String[]::new);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item_name;
        private final TextView item_amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_amount = itemView.findViewById(R.id.item_amount);
        }
    }
}
