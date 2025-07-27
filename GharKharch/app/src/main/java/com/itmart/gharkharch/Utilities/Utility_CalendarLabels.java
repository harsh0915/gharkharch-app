package com.itmart.gharkharch.Utilities;

import java.util.Arrays;

public class Utility_CalendarLabels {

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
}
