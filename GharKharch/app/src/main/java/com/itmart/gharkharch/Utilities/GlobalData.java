package com.itmart.gharkharch.Utilities;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import com.itmart.gharkharch.DatabaseHelper.DatabaseHelper;

public class GlobalData {

    private final Context context;
    private List<Integer> expensesData;
    private static GlobalData instance;

    private GlobalData(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized GlobalData getInstance(Context context) {
        if (instance == null) {
            instance = new GlobalData(context);
        }
        return instance;
    }

    public List<Integer> getExpensesData() {
        expensesData = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            expensesData.add(i);
        }
        return expensesData;
    }

    public void generateSnackBar(View view, String msg) {
        new Handler(Looper.getMainLooper()).post(() -> Snackbar.make(view, msg, 500).show());
    }

    public void generateToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public void close_keyboard(View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    //db ops
    public void edit_category(int id, String edited_cat_name) {
        try {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
            String where_clause = DatabaseHelper.COLUMN_CATEGORY_ID + " = ?";
            String[] where_args = new String[]{String.valueOf(id)};
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, edited_cat_name);

            int rows = db.update(DatabaseHelper.TABLE_CATEGORIES, values, where_clause, where_args);
            if (rows == 0)
                Log.d("harshTagSqlQuery", "Something went wrong while updating the cat table. Rows updated : " + id);
            else
                Log.d("harshTagSqlQuery", "Rows updated successfully. Rows updated : " + id);

        } catch (Exception e) {
            Log.i("harshTagSqlQuery", "Something went wrong!\nError : " + e.getMessage());
        }
    }

    public void edit_sub_category(int id, String edited_sub_cat_name) {
        try {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
            String where_clause = DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = ?";
            String[] where_args = new String[]{String.valueOf(id)};
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME, edited_sub_cat_name);

            int rows = db.update(DatabaseHelper.TABLE_SUB_CATEGORIES, values, where_clause, where_args);
            if (rows == 0)
                Log.d("harshTagSqlQuery", "Something went wrong while updating the sub-cat table. Rows updated : " + id);
            else
                Log.d("harshTagSqlQuery", "Rows updated successfully. Rows updated : " + id);

        } catch (Exception e) {
            Log.i("harshTagSqlQuery", "Something went wrong!\nError : " + e.getMessage());
        }
    }

    public void edit_expense(int id, int edited_amount) {
        try {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
            String where_clause = DatabaseHelper.COLUMN_EXPENSE_ID + " = ?";
            String[] where_args = new String[]{String.valueOf(id)};
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, edited_amount);

            int rows = db.update(DatabaseHelper.TABLE_EXPENSES, values, where_clause, where_args);
            if (rows == 0)
                Log.d("harshTagSqlQuery", "Something went wrong while updating the expense table. Rows updated : " + id);
            else
                Log.d("harshTagSqlQuery", "Rows updated successfully. Rows updated : " + id);

        } catch (Exception e) {
            Log.i("harshTagSqlQuery", "Something went wrong!\nError : " + e.getMessage());
        }
    }

    public void delete_item(int id, int item_type) {
        try {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
            String where_clause;
            String[] where_args;
            int rows;
            switch (item_type) {

                case 1:
                    where_clause = DatabaseHelper.COLUMN_CATEGORY_ID + " = ?";
                    where_args = new String[]{String.valueOf(id)};
                    rows = db.delete(DatabaseHelper.TABLE_CATEGORIES, where_clause, where_args);
                    if (rows == 0)
                        Log.d("harshTagSqlQuery", "Something went wrong while deleting the rows from category table.");
                    else
                        Log.d("harshTagSqlQuery", "Rows updated successfully. Rows updated : " + id);
                    break;

                case 2:
                    where_clause = DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = ?";
                    where_args = new String[]{String.valueOf(id)};
                    rows = db.delete(DatabaseHelper.TABLE_SUB_CATEGORIES, where_clause, where_args);
                    if (rows == 0)
                        Log.d("harshTagSqlQuery", "Something went wrong while deleting the rows from sub-category table.");
                    else
                        Log.d("harshTagSqlQuery", "Rows delete successfully.");
                    break;

                case 3:
                    where_clause = DatabaseHelper.COLUMN_EXPENSE_ID + " = ?";
                    where_args = new String[]{String.valueOf(id)};
                    rows = db.delete(DatabaseHelper.TABLE_EXPENSES, where_clause, where_args);
                    if (rows == 0)
                        Log.d("harshTagSqlQuery", "Something went wrong while deleting the rows from expense table.");
                    else
                        Log.d("harshTagSqlQuery", "Row delete successfully.");
                    break;
            }
        } catch (Exception e) {
            Log.i("harshTagSqlQuery", "Something went wrong!\nError : " + e.getMessage());
        }
    }
}
