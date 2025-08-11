package com.itmart.gharkharch.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import com.itmart.gharkharch.R;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final String TAG = "DatabaseDebugger";
    private final Context context;
    private static DatabaseHelper instance;
    private static final String DATABASE_NAME = "ghar_kharch_db";
    private static final int DATABASE_VERSION = 1;

    //    tables
    public static String TABLE_CATEGORIES = "categories";
    public static String TABLE_SUB_CATEGORIES = "sub_categories";
    public static String TABLE_EXPENSES = "expenses";

    //    fields for categories
    public static String COLUMN_CATEGORY_ID = "cat_id";
    public static String COLUMN_CATEGORY_NAME = "cat_name";

    //    fields for sub categories
    public static String COLUMN_SUB_CATEGORY_ID = "sub_cat_id";
    public static String COLUMN_SUB_CATEGORY_NAME = "sub_cat_name";
    public static String COLUMN_UNDER_CATEGORY = "under_category";

    //    fields for expense table
    public static String COLUMN_EXPENSE_ID = "expense_id";
    public static String COLUMN_UNDER_SUB_CATEGORY = "under_sub_category";
    public static String COLUMN_EXPENSE_AMOUNT = "expense_amount";
    public static String COLUMN_EXPENSE_DATE = "expense_date";
    public static String COLUMN_EXPENSE_MODE = "expense_mode";
    public static String COLUMN_EXPENSE_REMARKS = "expense_remarks";

    //create tables
    public String CREATE_CATEGORIES_TABLE = "create table " + TABLE_CATEGORIES + "(" +
            COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CATEGORY_NAME + " TEXT UNIQUE );";
    public String CREATE_SUB_CATEGORY_TABLE = "create table " + TABLE_SUB_CATEGORIES + "(" +
            COLUMN_SUB_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SUB_CATEGORY_NAME + " TEXT UNIQUE, " +
            COLUMN_UNDER_CATEGORY + " INTEGER NOT NULL, " +
            " FOREIGN KEY (" + COLUMN_UNDER_CATEGORY + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_CATEGORY_ID + ") " +
            "ON DELETE RESTRICT )";
    public String CREATE_EXPENSE_TABLE = "create table " + TABLE_EXPENSES + "(" +
            COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_UNDER_SUB_CATEGORY + " INTEGER NOT NULL, " +
            COLUMN_EXPENSE_AMOUNT + " INTEGER NOT NULL, " +
            COLUMN_EXPENSE_DATE + " INTEGER NOT NULL, " +
            COLUMN_EXPENSE_MODE + " INTEGER DEFAULT 0, " +
            COLUMN_EXPENSE_REMARKS + " TEXT DEFAULT NULL, " +
            " FOREIGN KEY (" + COLUMN_UNDER_SUB_CATEGORY + ") REFERENCES " + TABLE_SUB_CATEGORIES + "(" + COLUMN_SUB_CATEGORY_ID + ") " +
            "ON DELETE RESTRICT )";

    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    //public method to get the single instance of the database helper
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_CATEGORIES_TABLE);
            db.execSQL(CREATE_SUB_CATEGORY_TABLE);
            db.execSQL(CREATE_EXPENSE_TABLE);
            Log.i(TAG, "Successful table creation!");
            importCSVToDB(context.getResources().openRawResource(R.raw.cat), TABLE_CATEGORIES, db);
            importCSVToDB(context.getResources().openRawResource(R.raw.subcat), TABLE_SUB_CATEGORIES, db);
            Log.i(TAG, "Successfully parsed csv files to db!");
        } catch (SQLException e) {
            Log.e(TAG, "Sql table creation error!\nError : " + e.getMessage());
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
//            if(oldVersion<2){}
            db.setTransactionSuccessful();
        } catch (RuntimeException e) {
            Log.e(TAG, "Database Updating Failed : Updating Disturbed Data Integrity.");
            throw new RuntimeException("Database upgrade failed", e); // Crash on failure for data integrity
        } finally {
            db.endTransaction();
        }
    }

    private boolean doesColumnExist(SQLiteDatabase db, String tableName, String columnName) {
        boolean exists = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("pragma table_info (" + tableName + ")", null);
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    if (columnName.equals(name)) {
                        exists = true;
                        break;
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Database Updating Failed : Updating Disturbed Data Integrity.");
            throw new RuntimeException("Failed to check column existence", e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return exists;
    }

//    public void importCSVToDB(String csvFilePath, String tableName) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(csvFilePath));
//            db.beginTransaction();
//
//            String line;
//            String[] columns = null;
//            boolean isFirstLine = true;
//
//            while ((line = reader.readLine()) != null) {
//                String[] values = line.split(",");
//
//                // First line is header
//                if (isFirstLine) {
//                    columns = values;
//                    isFirstLine = false;
//                    continue;
//                }
//
//                // Prepare ContentValues
//                ContentValues contentValues = new ContentValues();
//                for (int i = 0; i < columns.length && i < values.length; i++) {
//                    contentValues.put(columns[i], values[i]);
//                }
//
//                db.insert(tableName, null, contentValues);
//            }
//            db.setTransactionSuccessful();
//        } catch (Exception e) {
//            Log.e("DatabaseDebugger", Log.getStackTraceString(e));
//        } finally {
//            if (db.inTransaction()) {
//                db.endTransaction();
//            }
//            try {
//                if (reader != null) {
//                    reader.close();
//                }
//            } catch (IOException e) {
//                Log.e("DatabaseDebugger", Log.getStackTraceString(e));
//            }
//        }
//    }

    public void importCSVToDB(InputStream inputStream, String tableName, SQLiteDatabase db) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            db.beginTransaction();

            String line;
            String[] columns = null;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (isFirstLine) {
                    columns = values;
                    isFirstLine = false;
                    continue;
                }

                ContentValues contentValues = new ContentValues();
                for (int i = 0; i < columns.length && i < values.length; i++) {
                    contentValues.put(columns[i], values[i]);
                }

                db.insert(tableName, null, contentValues);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
            try {
                if (reader != null) reader.close();
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
    }

}
