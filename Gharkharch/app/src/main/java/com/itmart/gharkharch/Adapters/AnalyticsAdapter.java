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

import com.itmart.gharkharch.DataModels.ItemForAnalysis;
import com.itmart.gharkharch.DataModels.SortBy;
import com.itmart.gharkharch.R;
import com.itmart.gharkharch.Utilities.Utility_Calendar;

import java.util.List;

public class AnalyticsAdapter extends RecyclerView.Adapter<AnalyticsAdapter.ViewHolder> {

    private final Context context;
    private final List<ItemForAnalysis> dataList;
    private final SortBy sortBy;
    private String[] xAxisLabels;
    private final int year;

    public AnalyticsAdapter(Context context, List<ItemForAnalysis> dataList, SortBy sortBy, int year) {
        this.context = context;
        this.dataList = dataList;
        this.sortBy = sortBy;
        this.year = year;
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

        ItemForAnalysis item = dataList.get(position);
        String date;
        if (sortBy == Month) {
            date = xAxisLabels[position] + ", " + year;
        } else {
            date = item.getName() + ", " + xAxisLabels[position] + ", " + year;
        }
        holder.item_name.setText(date);
        holder.item_amount.setText("\u20B9" + item.getAmount());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void getXAxisLabelsFromDataList() {
        switch (sortBy) {
            case Date:
                xAxisLabels = dataList.stream().map(item -> item != null ? item.getName() : "").toArray(String[]::new);
                break;

            case Month:
            default:
                xAxisLabels = Utility_Calendar.getMonths();
                break;
        }
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
