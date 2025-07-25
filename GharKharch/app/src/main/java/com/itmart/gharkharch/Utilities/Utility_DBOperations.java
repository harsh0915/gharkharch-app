package com.itmart.gharkharch.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import com.itmart.gharkharch.DatabaseHelper.DatabaseHelper;

public class Utility_DBOperations {


    private final Context context;
    private static Utility_DBOperations instance;

    private Utility_DBOperations(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized Utility_DBOperations getInstance(Context context) {
        if (instance == null) {
            instance = new Utility_DBOperations(context);
        }
        return instance;
    }

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

    public void edit_expense(int id, String expense_name, int under_sub_cat, int edited_amount, long date, int mode_of_payment, String expense_remarks) {
        try {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
            String where_clause = DatabaseHelper.COLUMN_EXPENSE_ID + " = ?";
            String[] where_args = new String[]{String.valueOf(id)};
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_EXPENSE_NAME, expense_name);
            values.put(DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY, under_sub_cat);
            values.put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, edited_amount);
            values.put(DatabaseHelper.COLUMN_EXPENSE_DATE, date);
            values.put(DatabaseHelper.COLUMN_EXPENSE_MODE, mode_of_payment);
            values.put(DatabaseHelper.COLUMN_EXPENSE_REMARKS, expense_remarks);

            int rows = db.update(DatabaseHelper.TABLE_EXPENSES, values, where_clause, where_args);
            if (rows == 0)
                Log.d("harshTagSqlQuery", "Something went wrong while updating the expense table. Rows updated : " + id);
            else
                Log.d("harshTagSqlQuery", "Rows updated successfully. Rows updated : " + id);

        } catch (Exception e) {
            Log.i("harshTagSqlQuery", "Something went wrong!\nError : " + e.getMessage());
        }
    }

    public void delete_item(int id, int item_type, View view) {
        try {
            SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();
            db.setForeignKeyConstraintsEnabled(true);
            String where_clause;
            String[] where_args;
            int rows;
            switch (item_type) {

                case 1:
                    try {
                        where_clause = DatabaseHelper.COLUMN_CATEGORY_ID + " = ?";
                        where_args = new String[]{String.valueOf(id)};
                        rows = db.delete(DatabaseHelper.TABLE_CATEGORIES, where_clause, where_args);
                        if (rows == 0)
                            Log.d("harshTagSqlQuery", "Something went wrong while deleting the rows from category table.");
                        else
                            Log.d("harshTagSqlQuery", "Rows updated successfully. Rows updated : " + id);
                    } catch (SQLiteConstraintException e) {
                        if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY constraint failed")) {
                            Utility_UiAlerts.generateSnackBar(view, "Cannot remove category as it is being used in an expense head.");
                        } else {
                            Utility_UiAlerts.generateSnackBar(view, "Constraint Error : " + e.getMessage());
                        }
                    }
                    break;

                case 2:
                    try {
                        where_clause = DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = ?";
                        where_args = new String[]{String.valueOf(id)};
                        rows = db.delete(DatabaseHelper.TABLE_SUB_CATEGORIES, where_clause, where_args);
                        if (rows == 0)
                            Log.d("harshTagSqlQuery", "Something went wrong while deleting the rows from sub-category table.");
                        else
                            Log.d("harshTagSqlQuery", "Rows delete successfully.");
                    } catch (SQLiteConstraintException e) {
                        if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY constraint failed")) {
                            Utility_UiAlerts.generateSnackBar(view, "Cannot remove head as it is being used in a ledger entry.");
                        } else {
                            Utility_UiAlerts.generateSnackBar(view, "Constraint Error : " + e.getMessage());
                        }
                    }
                    break;

                case 3:
                    try {
                        where_clause = DatabaseHelper.COLUMN_EXPENSE_ID + " = ?";
                        where_args = new String[]{String.valueOf(id)};
                        rows = db.delete(DatabaseHelper.TABLE_EXPENSES, where_clause, where_args);
                        if (rows == 0)
                            Log.d("harshTagSqlQuery", "Something went wrong while deleting the rows from expense table.");
                        else
                            Log.d("harshTagSqlQuery", "Row delete successfully.");
                    } catch (SQLiteConstraintException e) {
                        Utility_UiAlerts.generateSnackBar(view, "Constraint Error : " + e.getMessage());
                    }
                    break;

            }
        } catch (Exception e) {
            Log.i("harshTagSqlQuery", "Something went wrong!\nError : " + e.getMessage());
        }
    }
}
