package com.itmart.gharkharch.Adapters;

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
    private GlobalData globalData;
    private DB_Interface db_listener;

    public interface DB_Interface {
        void database_updated();
//        void edit_item();
    }

    public SimpleListAdapter(Context context, List<GenericItem> list, GenericItemType item_type, DB_Interface db_listener) {
        this.context = context;
        this.list = list;
        this.item_type = item_type;
        this.globalData = GlobalData.getInstance(context);
        this.db_listener=db_listener;
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

        holder.delete_item.setOnClickListener(v -> {
            fun_delete_item(item.get_item_id());
        });

        holder.edit_item.setOnClickListener(v -> {
            fun_edit_item(item.get_item_id(), item.get_item_name(), position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void fun_delete_item(int itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialog_layout = LayoutInflater.from(context).inflate(R.layout.dialog_generic, null);
        builder.setView(dialog_layout);

        Dialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();
    }

    private void fun_edit_item(int item_id, String item_name, int position) {
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

        text_editor.setText(item_name);
        positive_btn.setText("Ok");

        positive_btn.setOnClickListener(v -> {
            switch (item_type) {
                case Category:
                    globalData.edit_category(item_id, text_editor.getText().toString());
                    break;
                case Sub_Category:
                    // Handle Sub_Category update logic
                    break;
                case Expenses:
                    // Handle Expenses update logic
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

        TextView item_name;
        ImageView delete_item, edit_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            delete_item = itemView.findViewById(R.id.delete_item);
            edit_item = itemView.findViewById(R.id.edit_item);
        }
    }
}
