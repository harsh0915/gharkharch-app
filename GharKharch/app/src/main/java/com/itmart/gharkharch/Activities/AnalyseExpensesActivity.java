package com.itmart.gharkharch.Activities;

import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.itmart.gharkharch.DataModels.DBOpsForAnalysisCallbackObject;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.DataModels.ItemForAnalysis;
import com.itmart.gharkharch.DataModels.SortBy;
import com.itmart.gharkharch.R;
import com.itmart.gharkharch.Utilities.Utility_Calendar;
import com.itmart.gharkharch.Utilities.Utility_DBOperations;
import com.itmart.gharkharch.Utilities.Utility_DateFormats;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnalyseExpensesActivity extends AppCompatActivity {

    private final int BAR_CHART = 0;
    private final int PIE_CHART = 1;
    private int currentChartType;
    private BarChart barChart;
    private ImageView toggleChartType;
    private Button confirmSortFilter;
    private LinearLayout sortFiltersContainer;
    private ImageView sortByDropdown;
    private ImageView dateForSorting;
    private PieChart pieChart;
    private RadioGroup sortFiltersRadioGroup;
    private RadioButton monthlyRB;
    private RadioButton dateRB;
    private List<ItemForAnalysis> expenseList;
    private Utility_DBOperations utilityDbOperations;
    private SortBy sortBy;
    private long selectedTimeInMillis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_expenses);
        initializeDataAContainers();
        initializeUiElements();
        setupClickListeners();
        fetchData();
    }

    private void initializeDataAContainers() {
        expenseList = new ArrayList<>();
        sortBy = SortBy.Month;
        currentChartType = BAR_CHART;
        selectedTimeInMillis = System.currentTimeMillis();
    }

    private void initializeUiElements() {
        barChart = findViewById(R.id.barChart);
        barChart.setNoDataText("Please wait your data is being loaded");
        barChart.setNoDataTextColor(getColor(R.color.primaryTextColor));
        toggleChartType = findViewById(R.id.toggleChartType);

        //sort radio button and layouts
        sortByDropdown = findViewById(R.id.sortByDropdown);
        dateForSorting = findViewById(R.id.dateForSorting);
        sortFiltersContainer = findViewById(R.id.sortFiltersContainer);
        sortFiltersRadioGroup = findViewById(R.id.sortFiltersRadioGroup);
        monthlyRB = findViewById(R.id.monthly);
        dateRB = findViewById(R.id.dateWise);
        confirmSortFilter = findViewById(R.id.confirmSortFilter);

        //default view states
        monthlyRB.setChecked(true);
    }

    private void fetchData() {
        utilityDbOperations = Utility_DBOperations.getInstance(this);
        utilityDbOperations.fetchExpenseForAnalysis(selectedTimeInMillis, sortBy, fetchExpenseCallback);

    }

    private void setupClickListeners() {

        toggleChartType.setOnClickListener(v -> {
            if (currentChartType == BAR_CHART) {
                toggleChartType.setImageResource(R.drawable.pie_chart_icon);
                currentChartType = PIE_CHART;
            } else {
                toggleChartType.setImageResource(R.drawable.bar_graph_icon);
                currentChartType = BAR_CHART;
            }
        });

        dateForSorting.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select a date").setSelection(selectedTimeInMillis).setTheme(R.style.custom_date_picker).build();

            datePicker.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

            datePicker.addOnPositiveButtonClickListener(selection -> {
                selectedTimeInMillis = selection;
            });
        });

        sortByDropdown.setOnClickListener(v -> {
            toggleSortFilterList();
        });

        confirmSortFilter.setOnClickListener(v -> {
            if (monthlyRB.getId() == sortFiltersRadioGroup.getCheckedRadioButtonId() && sortBy != SortBy.Month) {
                sortBy = SortBy.Month;
                utilityDbOperations.fetchExpenseForAnalysis(selectedTimeInMillis, sortBy, fetchExpenseCallback);
            } else if (dateRB.getId() == sortFiltersRadioGroup.getCheckedRadioButtonId() && sortBy != SortBy.Date) {
                sortBy = SortBy.Date;
                utilityDbOperations.fetchExpenseForAnalysis(selectedTimeInMillis, sortBy, fetchExpenseCallback);
            }
            toggleSortFilterList();
        });
    }

    private void toggleSortFilterList() {
        if (sortFiltersContainer.getVisibility() == View.GONE) {
            sortFiltersContainer.setVisibility(View.VISIBLE);
            sortFiltersContainer.setTranslationY(-20);
            sortFiltersContainer.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(100).start();
            sortByDropdown.animate().rotation(180).setInterpolator(new LinearInterpolator()).setDuration(200).start();
        } else {
            sortFiltersContainer.animate().translationY(-20).setInterpolator(new LinearInterpolator()).setDuration(100).withEndAction(() -> {
                sortFiltersContainer.setVisibility(View.GONE);
                sortFiltersContainer.setTranslationY(0);
            });
            sortByDropdown.animate().rotation(0).setInterpolator(new LinearInterpolator()).setDuration(200).start();
        }
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
        barDataSet.setHighlightEnabled(false);
        barDataSet.setLabel("Total Expense (\u20B9)");
        barDataSet.setColor(getColor(R.color.secondaryColor));
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
        leftAxis.setGranularity(500f);
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
        barChart.setScaleEnabled(false);
        barChart.setHighlightPerTapEnabled(false);
        barChart.setVisibleXRangeMaximum(5f);
        barChart.zoom(1.2f, 1f, 0f, 0f);
        barChart.setBorderColor(getColor(R.color.secondaryColor));
    }

    private String[] getXAxisLabelsFromDataList() {
        String[] xAxisLabels;
        switch (sortBy) {
            case Date:
                xAxisLabels = expenseList.stream()
                        .map(item -> item != null ? item.getName() : "")
                        .toArray(String[]::new);
                break;

            case Month:
            default:
                xAxisLabels = Utility_Calendar.getMonths();
                break;
        }
        return xAxisLabels;
    }

    private final Utility_DBOperations.Callback_DBOpsForAnalysis fetchExpenseCallback = new Utility_DBOperations.Callback_DBOpsForAnalysis() {
        @Override
        public void onSuccess(DBOpsForAnalysisCallbackObject callbackObject) {
            expenseList.clear();
            expenseList.addAll(callbackObject.getData());
            inflateBarChart();
        }

        @Override
        public void onFailure(Exception e) {

        }

        @Override
        public void onWarning() {

        }
    };
}