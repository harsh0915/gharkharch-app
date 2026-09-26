package com.itmart.gharkharch.Utilities;

import android.database.Cursor;
import android.util.Log;

public class Utility_DBDebug {

    private static final String TAG = "DatabaseDebugger"; // Use a consistent TAG for filtering in Logcat

    /**
     * Prints the contents of a Cursor to Logcat for debugging purposes.
     * Ensure the cursor is not null and has data before calling this.
     * Remember to close the cursor after calling this function.
     *
     * @param cursor The Cursor object to print.
     * @param queryDescription A short description of the query for context in the logs.
     */
    public static void printCursorData(Cursor cursor, String queryDescription) {
        if (cursor == null) {
            Log.e(TAG, "Cursor is NULL for query: " + queryDescription);
            return;
        }

        // Log the number of rows found
        Log.d(TAG, "--- Start Cursor Data for: " + queryDescription + " ---");
        Log.d(TAG, "Total Rows: " + cursor.getCount());

        // Check if cursor has any data
        if (cursor.moveToFirst()) {
            // Get column names for headers
            String[] columnNames = cursor.getColumnNames();
            StringBuilder headerRow = new StringBuilder("Columns: | ");
            for (String columnName : columnNames) {
                headerRow.append(columnName).append(" | ");
            }
            Log.d(TAG, headerRow.toString());

            int rowCount = 0;
            do {
                rowCount++;
                StringBuilder rowData = new StringBuilder("Row " + rowCount + ": | ");
                for (int i = 0; i < columnNames.length; i++) {
                    // Try to get string for all types. SQLite often converts to string.
                    // You might need more specific checks for BLOBs or large data.
                    try {
                        rowData.append(cursor.getString(i)).append(" | ");
                    } catch (Exception e) {
                        // Handle cases where getString might fail for certain types (e.g., BLOB)
                        rowData.append("[Error getting data] | ");
                        Log.e(TAG, "Error reading column " + columnNames[i] + ": " + e.getMessage());
                    }
                }
                Log.d(TAG, rowData.toString());
            } while (cursor.moveToNext());
        } else {
            Log.d(TAG, "No data found in cursor for query: " + queryDescription);
        }

        Log.d(TAG, "--- End Cursor Data for: " + queryDescription + " ---");
    }
}