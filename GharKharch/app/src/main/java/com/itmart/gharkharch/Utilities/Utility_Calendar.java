package com.itmart.gharkharch.Utilities;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class Utility_Calendar {

    private static final String[] MONTHS = {"Jan", "Feb", "Mar", "April", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};

    private static final String[] QUARTERS = {"Qrt1", "Qrt2", "Qrt3", "Qrt4"};

    private static final String[] WEEKS = new String[52];

    static {
        // Initialize weekly labels
        for (int i = 0; i < WEEKS.length; i++) {
            WEEKS[i] = "Week " + (i + 1);
        }
    }

    public static String[] getMonths() {
        return Arrays.copyOf(MONTHS, MONTHS.length);
    }

    public static String[] getQuarters() {
        return Arrays.copyOf(QUARTERS, QUARTERS.length);
    }

    public static String[] getWeeks() {
        return Arrays.copyOf(WEEKS, WEEKS.length);
    }

    public static Calendar getStartOfDay(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar getEndOfDay(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

    public static Calendar getStartOfMonth(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar getEndOfMonth(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }
}
