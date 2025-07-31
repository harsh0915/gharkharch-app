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
import com.itmart.gharkharch.DataModels.DBOpsForAnalysisCallbackObject;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.DataModels.ItemForAnalysis;
import com.itmart.gharkharch.DataModels.SortBy;
import com.itmart.gharkharch.R;
import com.itmart.gharkharch.Utilities.Utility_Calendar;
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
    private RadioButton dateRB;
    private List<ItemForAnalysis> ListOfItemsForAnalysis;
    private List<ItemForAnalysis> categoriesList;
    private List<ItemForAnalysis> subCategoriesList;
    private List<ItemForAnalysis> expenseList;
    private Utility_DBOperations utilityDbOperations;
    private GenericItemType chartFilter;
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
        ListOfItemsForAnalysis = new ArrayList<>();
        categoriesList = new ArrayList<>();
        subCategoriesList = new ArrayList<>();
        expenseList = new ArrayList<>();
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
        dateRB = findViewById(R.id.dateWise);
        confirmSortFilter = findViewById(R.id.confirmSortFilter);

        //default view states
        categoriesRB.setChecked(true);
        monthlyRB.setChecked(true);
        dateRB.setVisibility(View.GONE);
        quarterlyRB.setVisibility(View.GONE);
    }

    private void fetchData() {
        utilityDbOperations = Utility_DBOperations.getInstance(this);
        utilityDbOperations.fetchCategoriesForAnalysis(selectedTimeInMillis, sortBy, fetchCategoriesCallback);
        utilityDbOperations.fetchSubCategoriesForAnalysis(selectedTimeInMillis, sortBy, fetchSubCategoriesCallback);
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

        chartFiltersDropdown.setOnClickListener(v -> {
            toggleChartFilterList();
        });

        sortByDropdown.setOnClickListener(v -> {
            toggleSortFilterList();
        });

        confirmChartFilter.setOnClickListener(v -> {
            if (categoriesRB.getId() == chartFiltersRadioGroup.getCheckedRadioButtonId() && chartFilter != GenericItemType.Category) {
                chartFilter = GenericItemType.Category;
                ListOfItemsForAnalysis.clear();
                ListOfItemsForAnalysis.addAll(categoriesList);
                dateRB.setVisibility(View.GONE);
                quarterlyRB.setVisibility(View.GONE);
                inflateBarChart();
            } else if (subCatRB.getId() == chartFiltersRadioGroup.getCheckedRadioButtonId() && chartFilter != GenericItemType.Sub_Category) {
                chartFilter = GenericItemType.Sub_Category;
                dateRB.setVisibility(View.GONE);
                quarterlyRB.setVisibility(View.GONE);
                ListOfItemsForAnalysis.clear();
                ListOfItemsForAnalysis.addAll(subCategoriesList);
                inflateBarChart();
            } else if (expenseRB.getId() == chartFiltersRadioGroup.getCheckedRadioButtonId() && chartFilter != GenericItemType.Expenses) {
                chartFilter = GenericItemType.Expenses;
                dateRB.setVisibility(View.VISIBLE);
                quarterlyRB.setVisibility(View.VISIBLE);
                ListOfItemsForAnalysis.clear();
                ListOfItemsForAnalysis.addAll(expenseList);
                inflateBarChart();
            }
            toggleChartFilterList();
        });

        confirmSortFilter.setOnClickListener(v -> {
            if (yearlyRB.getId() == sortFiltersRadioGroup.getCheckedRadioButtonId() && sortBy != SortBy.Year) {
                sortBy = SortBy.Year;
            } else if (quarterlyRB.getId() == sortFiltersRadioGroup.getCheckedRadioButtonId() && sortBy != SortBy.Quarter) {
                sortBy = SortBy.Quarter;
            } else if (monthlyRB.getId() == sortFiltersRadioGroup.getCheckedRadioButtonId() && sortBy != SortBy.Month) {
                sortBy = SortBy.Month;
            } else if (dateRB.getId() == sortFiltersRadioGroup.getCheckedRadioButtonId() && sortBy != SortBy.Date) {
                sortBy = SortBy.Date;
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
        barChart.fitScreen();
        barChart.setData(null);
        List<BarEntry> co_ordinates = new ArrayList<>();
        for (int i = 0; i < ListOfItemsForAnalysis.size(); i++) {
            co_ordinates.add(new BarEntry(i, ListOfItemsForAnalysis.get(i).getAmount()));
        }
        //dataset represents one set{} of co_ordinates
        BarDataSet barDataSet = new BarDataSet(co_ordinates, "BarDataSet");
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
        if (chartFilter == GenericItemType.Expenses) {
            switch (sortBy) {

                case Year:
                case Date:
                    xAxisLabels = ListOfItemsForAnalysis.stream()
                            .map(item -> item != null ? item.getName() : "")
                            .toArray(String[]::new);
                    break;

                case Quarter:
                    xAxisLabels = Utility_Calendar.getQuarters();
                    break;

                case Month:
                default:
                    xAxisLabels = Utility_Calendar.getMonths();
                    break;
            }
        } else {
            xAxisLabels = ListOfItemsForAnalysis.stream()
                    .map(item -> item != null ? item.getName() : "")
                    .toArray(String[]::new);
        }
        return xAxisLabels;
    }

    private final Utility_DBOperations.Callback_DBOpsForAnalysis fetchCategoriesCallback = new Utility_DBOperations.Callback_DBOpsForAnalysis() {

        @Override
        public void onSuccess(DBOpsForAnalysisCallbackObject callbackObject) {
            categoriesList.clear();
            categoriesList.addAll(callbackObject.getData());
            ListOfItemsForAnalysis.clear();
            ListOfItemsForAnalysis.addAll(categoriesList);
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