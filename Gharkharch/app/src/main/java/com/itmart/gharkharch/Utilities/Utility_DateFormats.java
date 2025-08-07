package com.itmart.gharkharch.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility_DateFormats {
    public static String convertLongToDate_DD_MM_YYYY(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static String convertLongToDate_DD_MM_YY(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static String convertLongToTime_hh_mm_ss(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH);
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public static String convertLongToTime_HH_mm_ss(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        Date date = new Date(timestamp);
        return sdf.format(date);
    }
}
