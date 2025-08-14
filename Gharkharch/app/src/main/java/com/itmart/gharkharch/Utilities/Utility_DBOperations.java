package com.itmart.gharkharch.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.itmart.gharkharch.DataModels.Category;
import com.itmart.gharkharch.DataModels.DBOpsForAnalysisCallbackObject;
import com.itmart.gharkharch.DataModels.DatabaseCallbackObject;
import com.itmart.gharkharch.DataModels.Expense;
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.ItemForAnalysis;
import com.itmart.gharkharch.DataModels.SortBy;
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

    public interface Callback_DBOpsForAnalysis {
        void onSuccess(DBOpsForAnalysisCallbackObject callbackObject);

        void onFailure(Exception e);

        void onWarning();
    }

    //add operations
    public void add_expense(int under_sub_category_id, int amount, long date, int mode_of_payment, String expense_remarks, Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            try {
                ContentValues values = new ContentValues();
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

    public void fetch_sub_category_list(Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            List<GenericItem> data_list = new ArrayList<>();
            HashSet<String> data_fast_lookup = new HashSet<>();
            try {
                String query = "select " +
                        " sub." + DatabaseHelper.COLUMN_SUB_CATEGORY_ID +
                        ", sub." + DatabaseHelper.COLUMN_SUB_CATEGORY_NAME +
                        ", sub." + DatabaseHelper.COLUMN_UNDER_CATEGORY +
                        ", cat." + DatabaseHelper.COLUMN_CATEGORY_NAME +
                        " from " + DatabaseHelper.TABLE_SUB_CATEGORIES + " as sub join " + DatabaseHelper.TABLE_CATEGORIES + " as cat " +
                        "on (sub." + DatabaseHelper.COLUMN_UNDER_CATEGORY + " = cat." + DatabaseHelper.COLUMN_CATEGORY_ID + ") " +
                        " order by " + DatabaseHelper.COLUMN_SUB_CATEGORY_NAME + " asc";
                Cursor cursor = null;

                try {
                    cursor = readable_db.rawQuery(query, null);

                    if (cursor.moveToFirst()) {
                        do {
                            int sub_cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUB_CATEGORY_ID));
                            String sub_cat_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME));
                            int cat_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_UNDER_CATEGORY));
                            String under_cat_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));

                            SubCategory subCategory = new SubCategory(sub_cat_id, sub_cat_name, cat_id, under_cat_name);
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

    public void fetch_expenses(Long startDate, Long endDate, Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            List<GenericItem> data_list = new ArrayList<>();
            try {
                String query = "select " +
                        "exp." + DatabaseHelper.COLUMN_EXPENSE_ID +
                        ", sub." + DatabaseHelper.COLUMN_SUB_CATEGORY_NAME +
                        ", exp." + DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY +
                        ", exp." + DatabaseHelper.COLUMN_EXPENSE_AMOUNT +
                        ", exp." + DatabaseHelper.COLUMN_EXPENSE_DATE +
                        ", exp." + DatabaseHelper.COLUMN_EXPENSE_MODE +
                        ", exp." + DatabaseHelper.COLUMN_EXPENSE_REMARKS +
                        " from " + DatabaseHelper.TABLE_EXPENSES + " as exp " +
                        " join " + DatabaseHelper.TABLE_SUB_CATEGORIES + " as sub " +
                        " on (exp." + DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY + " = sub." + DatabaseHelper.COLUMN_SUB_CATEGORY_ID + ") " +
                        " where exp." + DatabaseHelper.COLUMN_EXPENSE_DATE + " >= " + startDate + " and " +
                        " exp." + DatabaseHelper.COLUMN_EXPENSE_DATE + " <= " + endDate +
                        " order by " + DatabaseHelper.COLUMN_EXPENSE_DATE + " desc";
                Cursor cursor = null;

                try {
                    cursor = readable_db.rawQuery(query, null);

                    if (cursor.moveToFirst()) {
                        do {

                            int expense_id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EXPENSE_ID));
                            String expense_name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME));
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

//    public void fetchCategoriesForAnalysis(Long timeInMillis, SortBy sortBy, Callback_DBOpsForAnalysis callbackDbOpsForAnalysis) {
//        dbExecutor.execute(() -> {
//            String query;
//            List<ItemForAnalysis> data = new ArrayList<>();
//            try {
//                switch (sortBy) {
//
//                    case Year:
//                        query = "select " +
//                                " cat." + DatabaseHelper.COLUMN_CATEGORY_NAME +
//                                ", COALESCE(SUM ( exp." + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + "), 0) as TotalExpense" +
//                                " from " + DatabaseHelper.TABLE_CATEGORIES + " as cat" +
//                                " left join " + DatabaseHelper.TABLE_SUB_CATEGORIES + " as scat" +
//                                " on cat." + DatabaseHelper.COLUMN_CATEGORY_ID + " = scat." + DatabaseHelper.COLUMN_UNDER_CATEGORY +
//                                " left join " + DatabaseHelper.TABLE_EXPENSES + " as exp" +
//                                " on scat." + DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = exp." + DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY +
//                                " and exp." + DatabaseHelper.COLUMN_EXPENSE_DATE + " >= " + Utility_Calendar.getStartOfYear(timeInMillis).getTimeInMillis() +
//                                " and exp." + DatabaseHelper.COLUMN_EXPENSE_DATE + " < " + Utility_Calendar.getEndOfYear(timeInMillis).getTimeInMillis() +
//                                " group by cat." + DatabaseHelper.COLUMN_CATEGORY_NAME;
//                        break;
//
//                    case Quarter:
//                        if (quarter == -1) {
//                            callbackDbOpsForAnalysis.onWarning();
//                            return;
//                        }
//                        query = "select " +
//                                " cat." + DatabaseHelper.COLUMN_CATEGORY_NAME +
//                                ", SUM(exp." + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + ") as TotalExpense" +
//                                " from " + DatabaseHelper.TABLE_CATEGORIES + " as cat" +
//                                " join " + DatabaseHelper.TABLE_SUB_CATEGORIES + " as scat" +
//                                " on cat." + DatabaseHelper.COLUMN_CATEGORY_ID + " = scat." + DatabaseHelper.COLUMN_UNDER_CATEGORY +
//                                " join " + DatabaseHelper.TABLE_EXPENSES + " as exp" +
//                                " on scat." + DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = exp." + DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY +
//                                " where " + DatabaseHelper.COLUMN_EXPENSE_DATE + " >= " + Utility_Calendar.getFYQuarterStart(timeInMillis, quarter).getTimeInMillis() +
//                                " and " + DatabaseHelper.COLUMN_EXPENSE_DATE + " < " + Utility_Calendar.getFYQuarterEnd(timeInMillis, quarter).getTimeInMillis() +
//                                " group by cat." + DatabaseHelper.COLUMN_CATEGORY_NAME;
//                        break;
//
//                    case Month:
//                    default:
//                        query = "select " +
//                                " cat." + DatabaseHelper.COLUMN_CATEGORY_NAME +
//                                ", COALESCE(SUM ( exp." + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + "), 0) as TotalExpense" +
//                                " from " + DatabaseHelper.TABLE_CATEGORIES + " as cat" +
//                                " left join " + DatabaseHelper.TABLE_SUB_CATEGORIES + " as scat" +
//                                " on cat." + DatabaseHelper.COLUMN_CATEGORY_ID + " = scat." + DatabaseHelper.COLUMN_UNDER_CATEGORY +
//                                " left join " + DatabaseHelper.TABLE_EXPENSES + " as exp" +
//                                " on scat." + DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = exp." + DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY +
//                                " and exp." + DatabaseHelper.COLUMN_EXPENSE_DATE + " >= " + Utility_Calendar.getStartOfMonth(timeInMillis).getTimeInMillis() +
//                                " and exp." + DatabaseHelper.COLUMN_EXPENSE_DATE + " < " + Utility_Calendar.getEndOfMonth(timeInMillis).getTimeInMillis() +
//                                " group by cat." + DatabaseHelper.COLUMN_CATEGORY_NAME;
//                        break;
//
//                }
//                Cursor cursor = null;
//                try {
//                    cursor = readable_db.rawQuery(query, null);
//                    Utility_DBDebug.printCursorData(cursor, "harshBarChart");
//                    if (cursor.moveToFirst()) {
//                        do {
//                            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORY_NAME));
//                            int amount = cursor.getInt(cursor.getColumnIndexOrThrow("TotalExpense"));
//                            ItemForAnalysis item = new ItemForAnalysis(name, amount);
//                            data.add(item);
//                        } while (cursor.moveToNext());
//                    }
//                    callbackDbOpsForAnalysis.onSuccess(new DBOpsForAnalysisCallbackObject(data));
//                } catch (Exception e) {
//                    callbackDbOpsForAnalysis.onFailure(e);
//                    Log.i("DatabaseDebugger", "error : " + e.getMessage());
//                } finally {
//                    if (cursor != null) cursor.close();
//                }
//            } catch (Exception e) {
//                callbackDbOpsForAnalysis.onFailure(e);
//            }
//        });
//    }

//    public void fetchSubCategoriesForAnalysis(Long timeInMillis, SortBy sortBy, Callback_DBOpsForAnalysis callbackDbOpsForAnalysis) {
//        dbExecutor.execute(() -> {
//            String query;
//            List<ItemForAnalysis> data = new ArrayList<>();
//            try {
//                switch (sortBy) {
//
//                    case Year:
//                        query = "select " +
//                                " scat." + DatabaseHelper.COLUMN_SUB_CATEGORY_NAME +
//                                ", COALESCE(SUM ( exp." + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + "), 0) as TotalExpense" +
//                                " from " + DatabaseHelper.TABLE_SUB_CATEGORIES + " as scat" +
//                                " left join " + DatabaseHelper.TABLE_EXPENSES + " as exp" +
//                                " on scat." + DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = exp." + DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY +
//                                " and exp." + DatabaseHelper.COLUMN_EXPENSE_DATE + " >= " + Utility_Calendar.getStartOfYear(timeInMillis).getTimeInMillis() +
//                                " and exp." + DatabaseHelper.COLUMN_EXPENSE_DATE + " < " + Utility_Calendar.getEndOfYear(timeInMillis).getTimeInMillis() +
//                                " group by " + DatabaseHelper.COLUMN_SUB_CATEGORY_NAME;
//                        break;
//
//                    case Quarter:
//                        if (quarter == -1) {
//                            callbackDbOpsForAnalysis.onWarning();
//                            return;
//                        }
//                        query = "select " +
//                                " scat." + DatabaseHelper.COLUMN_SUB_CATEGORY_NAME +
//                                ", SUM ( exp." + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + ") as TotalExpense" +
//                                " from " + DatabaseHelper.TABLE_SUB_CATEGORIES + " as scat" +
//                                " join " + DatabaseHelper.TABLE_EXPENSES + " as exp" +
//                                " on scat." + DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = exp." + DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY +
//                                " where " + DatabaseHelper.COLUMN_EXPENSE_DATE + " >= " + Utility_Calendar.getFYQuarterStart(timeInMillis, quarter).getTimeInMillis() +
//                                " and " + DatabaseHelper.COLUMN_EXPENSE_DATE + " < " + Utility_Calendar.getFYQuarterEnd(timeInMillis, quarter).getTimeInMillis() +
//                                " group by " + DatabaseHelper.COLUMN_SUB_CATEGORY_NAME;
//                        break;
//
//                    case Month:
//                    default:
//                        query = "select " +
//                                " scat." + DatabaseHelper.COLUMN_SUB_CATEGORY_NAME +
//                                ", COALESCE(SUM ( exp." + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + "), 0) as TotalExpense" +
//                                " from " + DatabaseHelper.TABLE_SUB_CATEGORIES + " as scat" +
//                                " left join " + DatabaseHelper.TABLE_EXPENSES + " as exp" +
//                                " on scat." + DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = exp." + DatabaseHelper.COLUMN_UNDER_SUB_CATEGORY +
//                                " and exp." + DatabaseHelper.COLUMN_EXPENSE_DATE + " >= " + Utility_Calendar.getStartOfMonth(timeInMillis).getTimeInMillis() +
//                                " and exp." + DatabaseHelper.COLUMN_EXPENSE_DATE + " < " + Utility_Calendar.getEndOfMonth(timeInMillis).getTimeInMillis() +
//                                " group by " + DatabaseHelper.COLUMN_SUB_CATEGORY_NAME;
//                        break;
//                }
//                Cursor cursor = null;
//                try {
//                    cursor = readable_db.rawQuery(query, null);
//                    Utility_DBDebug.printCursorData(cursor, "db analysis");
//                    if (cursor.moveToFirst()) {
//                        do {
//                            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME));
//                            int amount = cursor.getInt(cursor.getColumnIndexOrThrow("TotalExpense"));
//                            ItemForAnalysis item = new ItemForAnalysis(name, amount);
//                            data.add(item);
//                        } while (cursor.moveToNext());
//                    }
//                    callbackDbOpsForAnalysis.onSuccess(new DBOpsForAnalysisCallbackObject(data));
//                } catch (Exception e) {
//                    callbackDbOpsForAnalysis.onFailure(e);
//                    Log.i("DatabaseDebugger", "error : " + e.getMessage());
//                } finally {
//                    if (cursor != null) cursor.close();
//                }
//            } catch (Exception e) {
//                callbackDbOpsForAnalysis.onFailure(e);
//            }
//        });
//    }

    public void fetchExpenseForAnalysis(Long timeInMillis, SortBy sortBy, Callback_DBOpsForAnalysis callbackDbOpsForAnalysis) {
        dbExecutor.execute(() -> {
            String query;
            List<ItemForAnalysis> data = new ArrayList<>();
            try {
                switch (sortBy) {

//                    case Year:
//                        query = "select " +
//                                " strftime ('%Y', datetime(" + DatabaseHelper.COLUMN_EXPENSE_DATE + "/1000,'unixepoch')) as Time" +
//                                ", SUM(" + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + ") as TotalExpense" +
//                                " from " + DatabaseHelper.TABLE_EXPENSES +
//                                " group by Time" +
//                                " order by Time";
//                        break;

//                    case Quarter:
//                        query = "select " +
//                                " " + DatabaseHelper.COLUMN_EXPENSE_DATE +
//                                ", SUM(" + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + ") as TotalExpense" +
//                                " from " + DatabaseHelper.TABLE_EXPENSES +
//                                " where " + DatabaseHelper.COLUMN_EXPENSE_DATE + " >= " + Utility_Calendar.getFYQuarterStart(timeInMillis, 1) +
//                                " and " + DatabaseHelper.COLUMN_EXPENSE_DATE + " < " + Utility_Calendar.getFYQuarterEnd(timeInMillis, 1) +
//                                " group by Time" +
//                                " order by Time";
//                        break;

                    case Month:
                        query = " with months_tab AS (" +
                                " SELECT '01' AS Time UNION ALL SELECT '02' UNION ALL SELECT '03'" +
                                " UNION ALL SELECT '04' UNION ALL SELECT '05' UNION ALL SELECT '06'" +
                                " UNION ALL SELECT '07' UNION ALL SELECT '08' UNION ALL SELECT '09'" +
                                " UNION ALL SELECT '10' UNION ALL SELECT '11' UNION ALL SELECT '12' )" +
                                " select " +
                                " m.Time" +
                                " , COALESCE(SUM(" + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + "), 0) as TotalExpense" +
                                " from months_tab as m" +
                                " left join " + DatabaseHelper.TABLE_EXPENSES + " as e" +
                                " on strftime ('%m', datetime(e." + DatabaseHelper.COLUMN_EXPENSE_DATE + "/1000, 'unixepoch')) = m.Time" +
                                " and e." + DatabaseHelper.COLUMN_EXPENSE_DATE + " >= " + Utility_Calendar.getStartOfYear(timeInMillis).getTimeInMillis() +
                                " and e." + DatabaseHelper.COLUMN_EXPENSE_DATE + " < " + Utility_Calendar.getEndOfYear(timeInMillis).getTimeInMillis() +
                                " group by m.Time" +
                                " order by m.Time";
                        break;

                    case Date:
                    default:
                        query = " with recursive Temp_DateTable(Time) as (" +
                                " select date(datetime(" + Utility_Calendar.getStartOfMonth(timeInMillis).getTimeInMillis() + "/1000, 'unixepoch'))" +
                                " union all" +
                                " select date(Time, '+1 day')" +
                                " from Temp_DateTable" +
                                " where Time <= date(datetime(" + Utility_Calendar.getEndOfMonth(timeInMillis).getTimeInMillis() + " / 1000, 'unixepoch','-1 day'))" +
                                " )" +
                                " select" +
                                " strftime ('%d', Temp_DateTable.Time) as Time" +
                                " , COALESCE(SUM(e." + DatabaseHelper.COLUMN_EXPENSE_AMOUNT + "), 0)as TotalExpense" +
                                " from Temp_DateTable" +
                                " left join " + DatabaseHelper.TABLE_EXPENSES + " as e" +
                                " on date(datetime(e." + DatabaseHelper.COLUMN_EXPENSE_DATE + " / 1000, 'unixepoch')) = Temp_DateTable.Time " +
                                " group by Temp_DateTable.Time" +
                                " order by Temp_DateTable.Time";
                        break;
                }
                Cursor cursor = null;
                try {
                    cursor = readable_db.rawQuery(query, null);
//                    Log.i("DatabaseDebugger", " Date is :" + Utility_DateFormats.convertLongToDate_DD_MM_YY(timeInMillis));
//                    Utility_DBDebug.printCursorData(cursor, "db analysis");
                    if (cursor.moveToFirst()) {
                        do {
                            String name = cursor.getString(cursor.getColumnIndexOrThrow("Time"));
                            int amount = cursor.getInt(cursor.getColumnIndexOrThrow("TotalExpense"));
                            ItemForAnalysis item = new ItemForAnalysis(name, amount);
                            data.add(item);
                        } while (cursor.moveToNext());
                    }
                    callbackDbOpsForAnalysis.onSuccess(new DBOpsForAnalysisCallbackObject(data));
                } catch (Exception e) {
                    callbackDbOpsForAnalysis.onFailure(e);
                    Log.i("DatabaseDebugger", "error : " + e.getMessage());
                } finally {
                    if (cursor != null) cursor.close();
                }
            } catch (Exception e) {
                callbackDbOpsForAnalysis.onFailure(e);
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

    public void edit_sub_category(int id, int under_catId, String edited_sub_cat_name, Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            try {
                String where_clause = DatabaseHelper.COLUMN_SUB_CATEGORY_ID + " = ?";
                String[] where_args = new String[]{String.valueOf(id)};
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_SUB_CATEGORY_NAME, edited_sub_cat_name);
                values.put(DatabaseHelper.COLUMN_UNDER_CATEGORY, under_catId);
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

    public void edit_expense(int id, int under_sub_cat, int edited_amount, long date, int mode_of_payment, String expense_remarks, Callback_DBOperations callbackDbOperations) {
        dbExecutor.execute(() -> {
            try {
                String where_clause = DatabaseHelper.COLUMN_EXPENSE_ID + " = ?";
                String[] where_args = new String[]{String.valueOf(id)};
                ContentValues values = new ContentValues();
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
