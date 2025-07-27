package com.itmart.gharkharch.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import com.itmart.gharkharch.DataModels.Category;
import com.itmart.gharkharch.DataModels.DatabaseCallbackObject;
import com.itmart.gharkharch.DataModels.Expense;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.SubCategory;
import com.itmart.gharkharch.DatabaseHelper.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Utility_DBOperations {


    private final Context context;
    private static Utility_DBOperations instance;
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor();
    private final DatabaseHelper databaseHelper;
    private final SQLiteDatabase writeable_db;
    private final SQLiteDatabase readable_db;

    private Utility_DBOperations(Context context) {
        this.context = context.getApplicationContext();
        this.databaseHelper = DatabaseHelper.getInstance(this.context);
        this.writeable_db = databaseHelper.getWritableDatabase();
        this.readable_db = databaseHelper.getReadableDatabase();
        this.writeable_db.setForeignKeyConstraintsEnabled(true);
    }

    public static synchronized Utility_DBOperations getInstance(Context context) {
        if (instance == null) {
            instance = new Utility_DBOperations(context);
        }
        return instance;
    }

    public interface Callback_DBOperations {
        void onSuccess(DatabaseCallbackObject databaseObjectsList);

        void onFailure(Exception e);

        void onWarning();
    }

    //add operations
    public void add_expense(String expense_name, int under_sub_category_id, int amount, long date, int mode_of_payment, String expense_remarks, Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            try {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_EXPENSE_NAME, expense_name);
                values.put(DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY, under_sub_category_id);
                values.put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, amount);
                values.put(DatabaseHelper.COLUMN_EXPENSE_DATE, date);
                values.put(DatabaseHelper.COLUMN_EXPENSE_MODE, mode_of_payment);
                values.put(DatabaseHelper.COLUMN_EXPENSE_REMARKS, expense_remarks);

                writeable_db.insert(DatabaseHelper.TABLE_EXPENSES, null, values);
                callbackDbOperations.onSuccess(new DatabaseCallbackObject());
            } catch (Exception e) {
                callbackDbOperations.onFailure(e);

            }
        });
    }

    public void addSubCategory(String new_sub_category, int under_cat_id, Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            try {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME, new_sub_category.trim());
                values.put(DatabaseHelper.COLUMN_UNDER_CATEGORY, under_cat_id);
                try {
                    writeable_db.insertOrThrow(DatabaseHelper.TABLE_SUB_CATEGORIES, null, values);
                    callbackDbOperations.onSuccess(new DatabaseCallbackObject());
                } catch (SQLiteConstraintException e) {
                    callbackDbOperations.onWarning();
                }
            } catch (Exception e) {
                callbackDbOperations.onFailure(e);
            }
        });
    }

    public void addCategory(String new_category, Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            try {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, new_category.trim());
                try {
                    writeable_db.insertOrThrow(DatabaseHelper.TABLE_CATEGORIES, null, values);
                    callbackDbOperations.onSuccess(new DatabaseCallbackObject());
                } catch (SQLiteConstraintException e) {
                    callbackDbOperations.onWarning();
                }
            } catch (Exception e) {
                callbackDbOperations.onFailure(e);
            }
        });
    }

    //data list fetch
    public void fetch_expenses(Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            List<GenericItem> data_list = new ArrayList<>();
            try {
                String query = "select * from " + DatabaseHelper.TABLE_EXPENSES + " order by " + DatabaseHelper.COLUMN_EXPENSE_DATE + " desc";
                Cursor cursor = null;

                try {
                    cursor = readable_db.rawQuery(query, null);

                    if (cursor.moveToFirst()) {
                        do {
                            int expense_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_ID));
                            String expense_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_NAME));
                            int sub_cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY));
                            int expense_amount = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_AMOUNT));
                            long expense_date = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_DATE));
                            int expense_mode = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_MODE));
                            String expense_remarks = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_REMARKS));

                            Expense expense = new Expense(expense_id, expense_name, sub_cat_id, expense_amount, expense_date, expense_mode, expense_remarks);
                            data_list.add(expense);
                        } while (cursor.moveToNext());
                    }

                    callbackDbOperations.onSuccess(new DatabaseCallbackObject(data_list));
                } catch (Exception e) {
                    callbackDbOperations.onFailure(e);
                } finally {
                    if (cursor != null) cursor.close();
                }
            } catch (Exception e) {
                callbackDbOperations.onFailure(e);
            }
        });
    }

    public void fetch_sub_category_list(Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            List<GenericItem> data_list = new ArrayList<>();
            HashSet<String> data_fast_lookup = new HashSet<>();
            try {
                String query = "select * from " + DatabaseHelper.TABLE_SUB_CATEGORIES + " order by " + DatabaseHelper.COLUMN_SUB_CATEGORY_NAME + " asc";
                Cursor cursor = null;

                try {
                    cursor = readable_db.rawQuery(query, null);

                    if (cursor.moveToFirst()) {
                        do {
                            int sub_cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUB_CATEGORY_ID));
                            String sub_cat_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME));
                            int cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UNDER_CATEGORY));

                            SubCategory subCategory = new SubCategory(sub_cat_id, sub_cat_name, cat_id);
                            data_list.add(subCategory);
                            data_fast_lookup.add(sub_cat_name);

                        } while (cursor.moveToNext());
                    }
                    callbackDbOperations.onSuccess(new DatabaseCallbackObject(data_list, data_fast_lookup));
                } catch (Exception e) {
                    callbackDbOperations.onFailure(e);
                } finally {
                    if (cursor != null) cursor.close();
                }
            } catch (Exception e) {
                callbackDbOperations.onFailure(e);
            }
        });
    }

    public void fetch_category_list(Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            List<GenericItem> data_list = new ArrayList<>();
            HashSet<String> data_fast_lookup = new HashSet<>();
            try {
                SQLiteDatabase readable_db = databaseHelper.getReadableDatabase();

                String query = "select * from " + DatabaseHelper.TABLE_CATEGORIES + " order by " + DatabaseHelper.COLUMN_CATEGORY_NAME + " asc";
                Cursor cursor = null;

                try {
                    cursor = readable_db.rawQuery(query, null);

                    if (cursor.moveToFirst()) {
                        do {
                            int cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_ID));
                            String cat_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
                            Category category = new Category(cat_id, cat_name);
                            data_list.add(category);
                            data_fast_lookup.add(cat_name);
                        } while (cursor.moveToNext());
                    }
                    callbackDbOperations.onSuccess(new DatabaseCallbackObject(data_list, data_fast_lookup));
                } catch (Exception e) {
                    callbackDbOperations.onFailure(e);
                } finally {
                    if (cursor != null) cursor.close();
                }
            } catch (Exception e) {
                callbackDbOperations.onFailure(e);
            }
        });
    }

    //edit functions
    public void edit_category(int id, String edited_cat_name, Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            try {
                String where_clause = DatabaseHelper.COLUMN_CATEGORY_ID + " = ?";
                String[] where_args = new String[]{String.valueOf(id)};
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_CATEGORY_NAME, edited_cat_name);

                try {
                    writeable_db.update(DatabaseHelper.TABLE_CATEGORIES, values, where_clause, where_args);
                    callbackDbOperations.onSuccess(new DatabaseCallbackObject());
                } catch (SQLiteConstraintException e) {
                    callbackDbOperations.onWarning();
                }
            } catch (Exception e) {
                callbackDbOperations.onFailure(e);
            }
        });
    }

    public void edit_sub_category(int id, String edited_sub_cat_name, Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            try {
                String where_clause = DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = ?";
                String[] where_args = new String[]{String.valueOf(id)};
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME, edited_sub_cat_name);
                try {
                    writeable_db.update(DatabaseHelper.TABLE_SUB_CATEGORIES, values, where_clause, where_args);
                    callbackDbOperations.onSuccess(new DatabaseCallbackObject());
                } catch (SQLiteConstraintException e) {
                    callbackDbOperations.onWarning();
                }
            } catch (Exception e) {
                callbackDbOperations.onFailure(e);
            }
        });
    }

    public void edit_expense(int id, String expense_name, int under_sub_cat, int edited_amount, long date, int mode_of_payment, String expense_remarks, Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            try {
                String where_clause = DatabaseHelper.COLUMN_EXPENSE_ID + " = ?";
                String[] where_args = new String[]{String.valueOf(id)};
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_EXPENSE_NAME, expense_name);
                values.put(DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY, under_sub_cat);
                values.put(DatabaseHelper.COLUMN_EXPENSE_AMOUNT, edited_amount);
                values.put(DatabaseHelper.COLUMN_EXPENSE_DATE, date);
                values.put(DatabaseHelper.COLUMN_EXPENSE_MODE, mode_of_payment);
                values.put(DatabaseHelper.COLUMN_EXPENSE_REMARKS, expense_remarks);

                writeable_db.update(DatabaseHelper.TABLE_EXPENSES, values, where_clause, where_args);
                callbackDbOperations.onSuccess(new DatabaseCallbackObject());
            } catch (Exception e) {
                callbackDbOperations.onFailure(e);
            }
        });
    }

    //single delete function
    public void delete_item(int id, int item_type, Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            try {
                String where_clause;
                String[] where_args;
                int rows;
                switch (item_type) {

                    case 1:
                        try {
                            where_clause = DatabaseHelper.COLUMN_CATEGORY_ID + " = ?";
                            where_args = new String[]{String.valueOf(id)};
                            rows = writeable_db.delete(DatabaseHelper.TABLE_CATEGORIES, where_clause, where_args);
                            if (rows == 0)
                                Log.i("harshTagSqlQuery", "Something went wrong while deleting the rows from category table.");
                            else
                                Log.i("harshTagSqlQuery", "Rows updated successfully. Rows updated : " + id);
                        } catch (SQLiteConstraintException e) {
                            if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY constraint failed")) {
                                callbackDbOperations.onWarning();
                                return;
                            } else {
                                Log.e("harshTagDelete", "Constraint Error : " + e.getMessage());
                            }
                        }
                        break;

                    case 2:
                        try {
                            where_clause = DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = ?";
                            where_args = new String[]{String.valueOf(id)};
                            rows = writeable_db.delete(DatabaseHelper.TABLE_SUB_CATEGORIES, where_clause, where_args);
                            if (rows == 0)
                                Log.w("harshTagSqlQuery", "Something went wrong while deleting the rows from sub-category table.");
                            else
                                Log.i("harshTagSqlQuery", "Rows delete successfully.");
                        } catch (SQLiteConstraintException e) {
                            if (e.getMessage() != null && e.getMessage().contains("FOREIGN KEY constraint failed")) {
                                callbackDbOperations.onWarning();
                                return;
                            } else {
                                Log.e("harshTagDelete", "Constraint Error : " + e.getMessage());
                            }
                        }
                        break;

                    case 3:
                        try {
                            where_clause = DatabaseHelper.COLUMN_EXPENSE_ID + " = ?";
                            where_args = new String[]{String.valueOf(id)};
                            rows = writeable_db.delete(DatabaseHelper.TABLE_EXPENSES, where_clause, where_args);
                            if (rows == 0)
                                Log.i("harshTagSqlQuery", "Something went wrong while deleting the rows from expense table.");
                            else
                                Log.i("harshTagSqlQuery", "Row delete successfully.");
                        } catch (Exception e) {
                            callbackDbOperations.onFailure(e);
                        }
                        break;
                }

                callbackDbOperations.onSuccess(new DatabaseCallbackObject());
            } catch (Exception e) {
                Log.e("harshTagDelete", "Constraint Error : " + e.getMessage());
                callbackDbOperations.onFailure(e);
            }
        });
    }
}
