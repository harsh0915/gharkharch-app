package com.itmart.gharkharch.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.R;

import java.util.List;

import com.itmart.gharkharch.Utilities.GlobalData;

public class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.ViewHolder> {

    private final Context context;
    private final List<GenericItem> list;
    private final GenericItemType item_type;
    private final GlobalData globalData;
    private final DB_Interface db_listener;

    public interface DB_Interface {
        void database_updated();
    }

    public SimpleListAdapter(Context context, List<GenericItem> list, GenericItemType item_type, DB_Interface db_listener) {
        this.context = context;
        this.list = list;
        this.item_type = item_type;
        this.globalData = GlobalData.getInstance(context);
        this.db_listener = db_listener;
    }

    @NonNull
    @Override
    public SimpleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleListAdapter.ViewHolder holder, int position) {
        GenericItem item = list.get(position);

        holder.item_name.setText(item.get_item_name());

        if (item_type == GenericItemType.Expenses) {
            holder.item_amount.setVisibility(View.VISIBLE);
            holder.item_amount.setText("\u20B9" + item.get_item_amount());
        } else
            holder.item_amount.setVisibility(View.GONE);

        holder.delete_item.setOnClickListener(v -> {
            fun_delete_item(item.get_item_id(), item.get_item_name(), position);
        });

        holder.edit_item.setOnClickListener(v -> {
            fun_edit_item(item.get_item_id(), item.get_item_name(), item.get_item_amount(), position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fun_delete_item(int itemId, String itemName, int position) {
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

        dialog_title.setText("Are you sure you want to delete \"" + itemName + "\" ?");
        if (item_type.getValue() == 1) {
            dialog_msg.setText("You won't be able to delete it until you have no expense heads under it! ");
        } else if (item_type.getValue() == 2) {
            dialog_msg.setText("You won't be able to delete it until you have no expense under it! ");
        } else {
            dialog_msg.setVisibility(View.GONE);
        }
        positive_btn.setText("Delete");

        positive_btn.setOnClickListener(v -> {
            globalData.delete_item(itemId, item_type.getValue());
            dialog.dismiss();
            db_listener.database_updated();
            notifyDataSetChanged();
        });

        negative_btn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void fun_edit_item(int item_id, String item_name, int item_amount, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialog_layout = LayoutInflater.from(context).inflate(R.layout.dialog_text_editor, null);
        builder.setView(dialog_layout);

        Dialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        EditText text_editor;
        TextView positive_btn, negative_btn;

        text_editor = dialog_layout.findViewById(R.id.text_editor);
        positive_btn = dialog_layout.findViewById(R.id.positive_btn);
        negative_btn = dialog_layout.findViewById(R.id.negative_btn);

        if (item_type == GenericItemType.Expenses)
            text_editor.setText(String.valueOf(item_amount));
        else
            text_editor.setText(item_name);

        positive_btn.setText("Ok");

        positive_btn.setOnClickListener(v -> {
            switch (item_type) {
                case Category:
                    globalData.edit_category(item_id, text_editor.getText().toString().trim());
                    break;
                case Sub_Category:
                    globalData.edit_sub_category(item_id, text_editor.getText().toString().trim());
                    break;
                case Expenses:
                    globalData.edit_expense(item_id, Integer.parseInt(text_editor.getText().toString().trim()));
                    break;
            }
            dialog.dismiss();
            db_listener.database_updated();
            notifyItemChanged(position);
        });

        negative_btn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_name, item_amount;
        ImageView delete_item, edit_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_amount = itemView.findViewById(R.id.item_amount);
            delete_item = itemView.findViewById(R.id.delete_item);
            edit_item = itemView.findViewById(R.id.edit_item);
        }
    }
}
