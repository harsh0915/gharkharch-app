package com.itmart.gharkharch.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.DataModels.ExpenseItem;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.R;
import com.itmart.gharkharch.Utilities.Utility_DateFormats;

import java.util.List;

public class ManageExpensesAdapter extends RecyclerView.Adapter<ManageExpensesAdapter.ViewHolder> {

    private final Context context;
    private final List<GenericItem> dataList;
    private final Click_Interface simpleList_clickListener;

    public interface Click_Interface {
        void delete_item(int item_id);

        void edit_item(GenericItem item);

        void database_updated();
    }

    public ManageExpensesAdapter(Context context, List<GenericItem> dataList, Click_Interface simpleListClickListener) {
        this.context = context;
        this.dataList = dataList;
        simpleList_clickListener = simpleListClickListener;
    }

    @NonNull
    @Override
    public ManageExpensesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_expense, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ManageExpensesAdapter.ViewHolder holder, int position) {
        ExpenseItem item = (ExpenseItem) dataList.get(position);
        holder.item_name.setText(item.get_item_name());
        holder.item_amount.setText("\u20B9" + item.get_item_amount());
        holder.item_date.setText(Utility_DateFormats.convertLongToDate_DD_MM_YY(item.get_date()));

        holder.item_amount.setVisibility(View.VISIBLE);
        holder.item_date.setVisibility(View.VISIBLE);

        holder.edit_item.setOnClickListener(v -> fun_delete_item(item));

        holder.delete_item.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public GenericItem getItemAtCurrentPos(int index) {
        return this.dataList.get(index);
    }

    private void fun_edit_item(GenericItem item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialog_layout = LayoutInflater.from(context).inflate(R.layout.dialog_expense_editor, null);
        builder.setView(dialog_layout);

        Dialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView positive_btn, negative_btn;

        positive_btn = dialog_layout.findViewById(R.id.positive_btn);
        negative_btn = dialog_layout.findViewById(R.id.negative_btn);

        positive_btn.setOnClickListener(v -> simpleList_clickListener.edit_item(item));

        negative_btn.setOnClickListener(v -> dialog.dismiss());
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void fun_delete_item(ExpenseItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialog_layout = LayoutInflater.from(context).inflate(R.layout.dialog_generic, null);
        builder.setView(dialog_layout);

        Dialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView dialog_title, dialog_msg, positive_btn, negative_btn;

        dialog_title = dialog_layout.findViewById(R.id.dialog_title);
        dialog_msg = dialog_layout.findViewById(R.id.dialog_msg);
        positive_btn = dialog_layout.findViewById(R.id.positive_btn);
        negative_btn = dialog_layout.findViewById(R.id.negative_btn);

        String titleText = "Delete ?";
        dialog_msg.setText(item.get_item_name() + " : \u20B9" + item.get_item_amount());

        dialog_title.setText(titleText);
        positive_btn.setText("Delete");

        positive_btn.setOnClickListener(v -> {
            simpleList_clickListener.delete_item(item.get_item_id());
            simpleList_clickListener.database_updated();
            dialog.dismiss();
        });

        negative_btn.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView item_name;
        private final TextView item_amount;
        private final TextView item_date;
        private final ImageView edit_item;
        private final ImageView delete_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_amount = itemView.findViewById(R.id.item_amount);
            item_date = itemView.findViewById(R.id.item_date);
            edit_item = itemView.findViewById(R.id.edit_item);
            delete_item = itemView.findViewById(R.id.delete_item);
        }
    }
}
