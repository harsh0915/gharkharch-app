package com.itmart.gharkharch.Activities;

import android.os.Bundle;
import android.util.Log;
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
import com.itmart.gharkharch.DataModels.DBOpsForAnalysisCallbackObject;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.DataModels.ItemForAnalysis;
import com.itmart.gharkharch.DataModels.SortBy;
import com.itmart.gharkharch.R;
import com.itmart.gharkharch.Utilities.Utility_DBOperations;

import java.util.ArrayList;
import java.util.List;

public class AnalyseExpensesActivity extends AppCompatActivity {

    private final int BAR_CHART = 0;
    private final int PIE_CHART = 1;
    private int currentChartType;
    private BarChart barChart;
    private ImageView toggleChartType;
    private Button confirmChartFilter;
    private Button confirmSortFilter;
    private LinearLayout chartFiltersContainer;
    private LinearLayout sortFiltersContainer;
    private ImageView chartFiltersDropdown;
    private ImageView sortByDropdown;
    private ImageView dateForSorting;
    private RadioGroup chartFiltersRadioGroup;
    private PieChart pieChart;
    private RadioButton expenseRB;
    private RadioButton subCatRB;
    private RadioButton categoriesRB;
    private RadioGroup sortFiltersRadioGroup;
    private RadioButton yearlyRB;
    private RadioButton quarterlyRB;
    private RadioButton monthlyRB;
    private List<ItemForAnalysis> coordinatesList;
    private List<ItemForAnalysis> categoriesList;
    private List<ItemForAnalysis> subCategoriesList;
    private List<ItemForAnalysis> expenseList;
    private Utility_DBOperations utilityDbOperations;
    private GenericItemType chartFilter;
    private SortBy sortBy;
    private int selectedQuarter;
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
        coordinatesList = new ArrayList<>();
        categoriesList = new ArrayList<>();
        subCategoriesList = new ArrayList<>();
        expenseList = new ArrayList<>();
        selectedQuarter = -1;
        sortBy = SortBy.Month;
        chartFilter = GenericItemType.Category;
        currentChartType = BAR_CHART;
        selectedTimeInMillis = System.currentTimeMillis();
    }

    private void initializeUiElements() {
        barChart = findViewById(R.id.barChart);
        barChart.setNoDataText("Please wait your data is being loaded");
        barChart.setNoDataTextColor(getColor(R.color.primaryTextColor));
        toggleChartType = findViewById(R.id.toggleChartType);

        //chart filters radio button and layouts
        chartFiltersDropdown = findViewById(R.id.chartFiltersDropdown);
        chartFiltersContainer = findViewById(R.id.chartFiltersContainer);
        chartFiltersRadioGroup = findViewById(R.id.chartFiltersRadioGroup);
        categoriesRB = findViewById(R.id.categoriesRB);
        subCatRB = findViewById(R.id.subCatRB);
        expenseRB = findViewById(R.id.expenseRB);
        confirmChartFilter = findViewById(R.id.confirmChartFilter);

        //sort radio button and layouts
        sortByDropdown = findViewById(R.id.sortByDropdown);
        dateForSorting = findViewById(R.id.dateForSorting);
        sortFiltersContainer = findViewById(R.id.sortFiltersContainer);
        sortFiltersRadioGroup = findViewById(R.id.sortFiltersRadioGroup);
        yearlyRB = findViewById(R.id.yearly);
        quarterlyRB = findViewById(R.id.quarterly);
        monthlyRB = findViewById(R.id.monthly);
        confirmSortFilter = findViewById(R.id.confirmSortFilter);

        //default view states
        categoriesRB.setChecked(true);
        monthlyRB.setChecked(true);
    }

    private void fetchData() {
        utilityDbOperations = Utility_DBOperations.getInstance(this);
        utilityDbOperations.fetchCategoriesForAnalysis(selectedTimeInMillis, sortBy, selectedQuarter, fetchCategoriesCallback);
        utilityDbOperations.fetchSubCategoriesForAnalysis(selectedTimeInMillis, sortBy, selectedQuarter, fetchSubCategoriesCallback);
        utilityDbOperations.fetchExpenseForAnalysis(selectedTimeInMillis, sortBy, selectedQuarter, fetchExpenseCallback);

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

        chartFiltersDropdown.setOnClickListener(v -> {
            toggleChartFilterList();
        });

        sortByDropdown.setOnClickListener(v -> {
            toggleSortFilterList();
        });

        confirmChartFilter.setOnClickListener(v -> {
            if (categoriesRB.getId() == chartFiltersRadioGroup.getCheckedRadioButtonId() && chartFilter != GenericItemType.Category) {
                chartFilter = GenericItemType.Category;
                coordinatesList.clear();
                coordinatesList.addAll(categoriesList);
                inflateBarChart();
            } else if (subCatRB.getId() == chartFiltersRadioGroup.getCheckedRadioButtonId() && chartFilter != GenericItemType.Sub_Category) {
                chartFilter = GenericItemType.Sub_Category;
                coordinatesList.clear();
                coordinatesList.addAll(subCategoriesList);
                inflateBarChart();
            } else {
                if (chartFilter != GenericItemType.Expenses) {
                    chartFilter = GenericItemType.Expenses;
                    coordinatesList.clear();
                    coordinatesList.addAll(expenseList);
                    inflateBarChart();
                }
            }
            toggleChartFilterList();
//            fetch the selected list type for xy coordinates than set the new xy coordinates for the chat to show and rcv
        });

        confirmSortFilter.setOnClickListener(v -> {
            if (yearlyRB.getId() == sortFiltersRadioGroup.getCheckedRadioButtonId() && sortBy != SortBy.Year) {
                sortBy = SortBy.Year;
            } else if (quarterlyRB.getId() == sortFiltersRadioGroup.getCheckedRadioButtonId() && sortBy != SortBy.Quarter) {
                sortBy = SortBy.Quarter;
            } else {
                if (sortBy != SortBy.Month) {
                    sortBy = SortBy.Month;
                }
            }
            toggleSortFilterList();
//            fetch the selected list type for xy coordinates than set the new xy coordinates for the chat to show and rcv
        });
    }

    private void toggleChartFilterList() {
        if (chartFiltersContainer.getVisibility() == View.GONE) {
            chartFiltersContainer.setVisibility(View.VISIBLE);
            chartFiltersContainer.setTranslationY(-20);
            chartFiltersContainer.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(100).start();
            chartFiltersDropdown.animate().rotation(180).setInterpolator(new LinearInterpolator()).setDuration(200).start();
        } else {
            chartFiltersContainer.animate().translationY(-20).setInterpolator(new LinearInterpolator()).setDuration(100).withEndAction(() -> {
                chartFiltersContainer.setVisibility(View.GONE);
                chartFiltersContainer.setTranslationY(0);
            });
            chartFiltersDropdown.animate().rotation(0).setInterpolator(new LinearInterpolator()).setDuration(200).start();
        }
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
        List<BarEntry> co_ordinates = new ArrayList<>();
        for (int i = 0; i < coordinatesList.size(); i++) {
            co_ordinates.add(new BarEntry(i, coordinatesList.get(i).getAmount()));
        }
        //dataset represents one set{} of co_ordinates
        BarDataSet barDataSet = new BarDataSet(co_ordinates, "Label");
        barDataSet.setHighlightEnabled(false);
        barDataSet.setLabel("Total Expense (\u20B9)");
        barDataSet.setColor(getColor(R.color.secondaryColor));
        barDataSet.setHighLightColor(R.color.highlightedBars);
        //bar data is the datatype accepted by the bar chart
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.7f);
        barData.setValueTextColor(getColor(R.color.primaryTextColorVariant));
        setupBarChartAttributes();
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void setupBarChartAttributes() {
        Description desc = new Description();
        desc.setEnabled(false);
        // -------------------- x axis of the bar chart --------------------
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(getColor(R.color.primaryTextColor));
        String[] xAxisLabels = getXAxisLabelsFromDataList();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setLabelCount(xAxisLabels.length, false);
        // -------------------- left y axis of bar chart --------------------
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(500f);
        xAxis.setGranularityEnabled(true);
        leftAxis.setTextColor(getColor(R.color.primaryTextColor));
        // -------------------- right y axis of bar chart --------------------
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setGranularity(500f);
        rightAxis.setTextColor(getColor(R.color.primaryTextColor));
        // -------------------- legend color is the label's legend that should match with the bar color --------------------
        Legend legend = barChart.getLegend();
        legend.setTextColor(getColor(R.color.primaryTextColor));
        // -------------------- setting up some bar attributes --------------------
        barChart.setFitBars(true);
        barChart.setPinchZoom(true);
        barChart.setVisibleXRangeMaximum(20f);
        barChart.setDescription(desc);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.setHighlightPerTapEnabled(false);
        barChart.setBorderColor(getColor(R.color.secondaryColor));
    }

    private String[] getXAxisLabelsFromDataList() {
        return coordinatesList.stream()
                .map(item -> item != null ? item.getName() : "")
                .toArray(String[]::new);
    }

    private final Utility_DBOperations.Callback_DBOpsForAnalysis fetchCategoriesCallback = new Utility_DBOperations.Callback_DBOpsForAnalysis() {

        @Override
        public void onSuccess(DBOpsForAnalysisCallbackObject callbackObject) {
            categoriesList.clear();
            categoriesList.addAll(callbackObject.getData());
            coordinatesList.clear();
            coordinatesList.addAll(categoriesList);
            inflateBarChart();
        }

        @Override
        public void onFailure(Exception e) {

        }

        @Override
        public void onWarning() {

        }
    };

    private final Utility_DBOperations.Callback_DBOpsForAnalysis fetchSubCategoriesCallback = new Utility_DBOperations.Callback_DBOpsForAnalysis() {
        @Override
        public void onSuccess(DBOpsForAnalysisCallbackObject callbackObject) {
            subCategoriesList.clear();
            subCategoriesList.addAll(callbackObject.getData());
        }

        @Override
        public void onFailure(Exception e) {

        }

        @Override
        public void onWarning() {

        }
    };

    private final Utility_DBOperations.Callback_DBOpsForAnalysis fetchExpenseCallback = new Utility_DBOperations.Callback_DBOpsForAnalysis() {
        @Override
        public void onSuccess(DBOpsForAnalysisCallbackObject callbackObject) {
            expenseList.clear();
            expenseList.addAll(callbackObject.getData());
        }

        @Override
        public void onFailure(Exception e) {

        }

        @Override
        public void onWarning() {

        }
    };
}