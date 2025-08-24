package com.itmart.gharkharch.Activities;

import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.itmart.gharkharch.DataModels.DBOpsForAnalysisCallbackObject;
import com.itmart.gharkharch.DataModels.GroupBy;
import com.itmart.gharkharch.DataModels.ItemForAnalysis;
import com.itmart.gharkharch.R;
import com.itmart.gharkharch.Utilities.Utility_Calendar;
import com.itmart.gharkharch.Utilities.Utility_DBOperations;

import java.util.ArrayList;
import java.util.List;

public class AnalyseExpensesActivity extends AppCompatActivity {

    private BarChart barChart;
    private ImageView help;
    private ImageView resetButton;
    private ImageView showYearPicker;
    private TextView selectedSortBy;
    private List<ItemForAnalysis> expenseList;
    private Utility_DBOperations utilityDbOperations;
    private int selectedYear;
    private int selectedMonth;
    private FrameLayout topBar;
    private GroupBy groupBy;
    private long selectedTimeInMillis;
    private boolean isAltered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_expenses);
        if (savedInstanceState != null) {
            groupBy = GroupBy.valueOf(savedInstanceState.getString("groupBy"));
            selectedTimeInMillis = savedInstanceState.getLong("selectedTimeInMillis");
            selectedYear = savedInstanceState.getInt("selectedYear");
            selectedMonth = savedInstanceState.getInt("selectedMonth");
            isAltered = savedInstanceState.getBoolean("isAltered");
        } else {
            groupBy = GroupBy.Month;
            selectedTimeInMillis = System.currentTimeMillis();
            selectedYear = Utility_Calendar.getYear(selectedTimeInMillis);
            selectedMonth = -1;
            isAltered = false;
        }
        initializeUiElements();
        setupClickListeners();
        fetchData();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            topBar.setVisibility(View.GONE);
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            topBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (groupBy == GroupBy.Month) {
            selectedSortBy.setText("Expenses for year : " + selectedYear);
        } else {
            selectedSortBy.setText("Expenses for month of : " + Utility_Calendar.getMonths()[selectedMonth]);
        }
        if (isAltered) {
            resetButton.setVisibility(View.VISIBLE);
        } else {
            resetButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("groupBy", groupBy.name());
        outState.putLong("selectedTimeInMillis", selectedTimeInMillis);
        outState.putInt("selectedYear", selectedYear);
        outState.putInt("selectedMonth", selectedMonth);
        outState.putBoolean("isAltered", isAltered);
    }

    private void initializeUiElements() {
        topBar = findViewById(R.id.topBar);
        barChart = findViewById(R.id.barChart);
        barChart.setNoDataText("Please wait your data is being loaded");
        barChart.setNoDataTextColor(getColor(R.color.primaryTextColor));
        help = findViewById(R.id.toggleChartType);
        resetButton = findViewById(R.id.remove_filters);
        selectedSortBy = findViewById(R.id.selectedSortBy);
        showYearPicker = findViewById(R.id.showYearPicker);
    }

    private void fetchData() {
        expenseList = new ArrayList<>();
        utilityDbOperations = Utility_DBOperations.getInstance(this);
        utilityDbOperations.fetchExpensesByGroup(selectedTimeInMillis, groupBy, fetchExpenseCallback);
    }

    private void setupClickListeners() {

        help.setOnClickListener(v -> showChartHelp());

        showYearPicker.setOnClickListener(v -> {
            if (groupBy == GroupBy.Month) {
                showYearPickerDialog();
            } else {
                showMonthPickerDialog();
            }
        });

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (groupBy == GroupBy.Date) return;

                if (e instanceof BarEntry) {
                    try {
                        isAltered = true;
                        selectedMonth = (int) e.getX();
                        selectedTimeInMillis = Utility_Calendar.getMonthInMillis(selectedMonth, selectedYear).getTimeInMillis();
                        groupBy = GroupBy.Date;
                        recreate();
                    } catch (Exception err) {
                        Log.i("ChartDebug", "Something went wrong ! ERROR : " + err.getMessage());
                    }
                }
            }

            @Override
            public void onNothingSelected() {
            }
        });

        resetButton.setOnClickListener(v -> resetFilters());
    }

    private void inflateBarChart() {
        barChart.clear();
        barChart.fitScreen();
        barChart.setData(null);
        List<BarEntry> co_ordinates = new ArrayList<>();
        for (int i = 0; i < expenseList.size(); i++) {
            co_ordinates.add(new BarEntry(i, expenseList.get(i).getAmount()));
        }
        //dataset represents one set{} of co_ordinates
        BarDataSet barDataSet = new BarDataSet(co_ordinates, "BarDataSet");
        barDataSet.setDrawValues(true);
        barDataSet.setValueTextSize(7f);
        barDataSet.setHighlightEnabled(true);
        barDataSet.setLabel("Total Expense (\u20B9)");
        barDataSet.setColor(getColor(R.color.secondaryColor));
        barDataSet.setHighLightColor(getColor(R.color.secondaryColor));
        //bar data is the datatype accepted by the bar chart
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f);
        barData.setValueTextColor(getColor(R.color.primaryTextColor));
        setupBarChartAttributes();
        barChart.setData(barData);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
        barChart.animateY(500);
    }

    private void setupBarChartAttributes() {
        // -------------------- x axis of the bar chart --------------------
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(getColor(R.color.primaryTextColor));
        String[] xAxisLabels = getXAxisLabelsFromDataList();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setLabelCount(5, false);
        // -------------------- left y axis of bar chart --------------------
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        leftAxis.setTextColor(getColor(R.color.primaryTextColor));
        // -------------------- right y axis of bar chart --------------------
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        // -------------------- legend color is the label's legend that should match with the bar color --------------------
        Legend legend = barChart.getLegend();
        legend.setTextColor(getColor(R.color.primaryTextColor));
        // -------------------- setting up some bar attributes --------------------
        Description desc = new Description();
        desc.setEnabled(false);
        barChart.setFitBars(true);
        barChart.setDescription(desc);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setScaleXEnabled(true);
        barChart.setScaleYEnabled(true);
        barChart.setVisibleXRangeMaximum(5f);
        barChart.zoom(1.2f, 1f, 0f, 0f);
        barChart.setBorderColor(getColor(R.color.secondaryColor));
    }

    private String[] getXAxisLabelsFromDataList() {
        String[] xAxisLabels;
        switch (groupBy) {
            case Date:
                xAxisLabels = expenseList.stream().map(item -> item != null ? item.getName() : "").toArray(String[]::new);
                break;

            case Month:
            default:
                xAxisLabels = Utility_Calendar.getMonths();
                break;
        }
        return xAxisLabels;
    }

    public void showYearPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_item_picker, null);
        builder.setView(view);

        NumberPicker yearPicker = view.findViewById(R.id.number_picker);
        Button okBtn = view.findViewById(R.id.okBtn);

        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(2100);
        yearPicker.setValue(selectedYear);

        Dialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        okBtn.setOnClickListener(btn -> {
            isAltered = true;
            selectedYear = yearPicker.getValue();
            selectedTimeInMillis = Utility_Calendar.getYearInMillis(selectedYear);
            dialog.dismiss();
            recreate();
        });

        dialog.show();
    }

    public void showMonthPickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_item_picker, null);
        builder.setView(view);

        NumberPicker monthPicker = view.findViewById(R.id.number_picker);
        NumberPicker yearPicker = view.findViewById(R.id.number_picker2);
        yearPicker.setVisibility(View.VISIBLE);
        Button okBtn = view.findViewById(R.id.okBtn);

        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(2100);
        yearPicker.setValue(selectedYear);

        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setValue(selectedMonth);
        monthPicker.setDisplayedValues(Utility_Calendar.getMonths());

        Dialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        okBtn.setOnClickListener(btn -> {
            isAltered = true;
            selectedMonth = monthPicker.getValue();
            selectedYear = yearPicker.getValue();
            selectedTimeInMillis = Utility_Calendar.getMonthInMillis(selectedMonth, selectedYear).getTimeInMillis();
            dialog.dismiss();
            recreate();
        });

        dialog.show();
    }

    public void showChartHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_bar_help, null);
        builder.setView(view);

        Dialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    public void resetFilters() {
        selectedTimeInMillis = System.currentTimeMillis();
        selectedYear = Utility_Calendar.getYear(selectedTimeInMillis);
        selectedMonth = 0;
        groupBy = GroupBy.Month;
        resetButton.setVisibility(View.GONE);
        isAltered = false;
        recreate();
    }

    private final Utility_DBOperations.Callback_DBOpsForAnalysis fetchExpenseCallback = new Utility_DBOperations.Callback_DBOpsForAnalysis() {
        @Override
        public void onSuccess(DBOpsForAnalysisCallbackObject callbackObject) {
            expenseList.clear();
            expenseList.addAll(callbackObject.getData());
            new Handler(Looper.getMainLooper()).post(() -> {
                inflateBarChart();
            });
        }

        @Override
        public void onFailure(Exception e) {

        }
    };
}