package com.itmart.gharkharch.DatabaseHelper;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;
    private static final String DATABASE_NAME = "ghar_kharch_db";
    private static final int DATABASE_VERSION = 1;

    //    tables
    public static String TABLE_CATEGORIES = "categories";
    public static String TABLE_SUBCATEGORIES = "sub_categories";
    public static String TABLE_EXPENSES = "expenses";

    //    fields for categories
    public static String COLUMN_CATEGORY_ID = "cat_id";
    public static String COLUMN_CATEGORY_NAME = "cat_name";
    public static String COLUMN_USER_ID = "user_id";

    //    fields for sub categories
    public static String COLUMN_SUB_CATEGORY_ID = "sub_cat_id";
    public static String COLUMN_SUB_CATEGORY_NAME = "cat_name";

    //    fields for expense table
    public static String COLUMN_EXPENSE_ID = "expense_id";
    public static String COLUMN_EXPENSE_AMOUNT = "expense_amount";
    public static String COLUMN_EXPENSE_DATE = "expense_date";
    public static String COLUMN_EXPENSE_MODE = "expense_mode";

    //    create tables
    public String CREATE_CATEGORIES_TABLE = "create table " + TABLE_CATEGORIES + "(" +
            COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CATEGORY_NAME + " TEXT UNIQUE );";
    public String CREATE_SUB_CATEGORY_TABLE = "create table " + TABLE_SUBCATEGORIES + "(" +
            COLUMN_SUB_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SUB_CATEGORY_NAME + " TEXT UNIQUE, " +
            COLUMN_CATEGORY_ID + " NOT NULL );";
    public String CREATE_EXPENSE_TABLE = "create table " + TABLE_EXPENSES +
            "(" + COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
            COLUMN_SUB_CATEGORY_ID + " INTEGER NOT NULL, " +
            COLUMN_EXPENSE_AMOUNT + " INTEGER NOT NULL, " +
            COLUMN_EXPENSE_DATE + " INTEGER NOT NULL, " +
            COLUMN_EXPENSE_MODE + " INTEGER DEFAULT 0);";

    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Public method to get the single instance of the database helper
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
            Log.d("harshTag", "Successful table creation!");
        } catch (SQLException e) {
            Log.d("harshTag", "Sql table creation error!\nError : " + e.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

}
