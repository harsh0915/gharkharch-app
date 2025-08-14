package com.itmart.gharkharch.Utilities;

import java.time.LocalDate;
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

    public static Calendar getFYQuarterStart(long financialYear, int quarter) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        int year = (int) financialYear;

        switch (quarter) {
            case 1: // Apr-Jun
                calendar.set(year, Calendar.APRIL, 1);
                break;
            case 2: // Jul-Sep
                calendar.set(year, Calendar.JULY, 1);
                break;
            case 3: // Oct-Dec
                calendar.set(year, Calendar.OCTOBER, 1);
                break;
            case 4: // Jan-Mar (next year)
                calendar.set(year + 1, Calendar.JANUARY, 1);
                break;
            default:
                throw new IllegalArgumentException("Quarter must be between 1 and 4");
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar getFYQuarterEnd(long financialYear, int quarter) {
        Calendar calendar = getFYQuarterStart(financialYear, quarter);

        switch (quarter) {
            case 1:
                calendar.set(Calendar.MONTH, Calendar.JUNE);
                break;
            case 2:
                calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
                break;
            case 3:
                calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                break;
            case 4:
                calendar.set(Calendar.MONTH, Calendar.MARCH);
                calendar.set(Calendar.YEAR, (int) financialYear + 1); // Q4 is next year
                break;
        }

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

    public static Calendar getMonthInMillis(int monthIndex, int year) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthIndex);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Long getYearInMillis(int year) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static int getMonth(Long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.MONTH);
    }

    public static int getYear(Long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        return calendar.get(Calendar.YEAR);
    }

    public static Calendar[] getYearRange() {
        Calendar now = Calendar.getInstance();
        Calendar[] range = new Calendar[2];

        Calendar start = (Calendar) now.clone();
        start.add(Calendar.YEAR, -10);
        range[0] = start;

        Calendar end = (Calendar) now.clone();
        end.add(Calendar.YEAR, 10);
        range[1] = end;

        return range;
    }

    public static Calendar getStartOfYear(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static Calendar getEndOfYear(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

}
