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
import com.itmart.gharkharch.DataModels.GenericItem;
import com.itmart.gharkharch.DataModels.GenericItemType;
import com.itmart.gharkharch.R;
import com.itmart.gharkharch.Utilities.Utility_Calendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalyseExpensesActivity extends AppCompatActivity {

    private final int BAR_CHART = 0;
    private final int PIE_CHART = 1;
    private int currentChartType;
    private GenericItemType selectedItemType;
    private RadioGroup itemRadioGroup;
    private List<GenericItem> x_y_coordinatesList;
    private BarChart barChart;
    private PieChart pieChart;
    private RadioButton expenseRB;
    private RadioButton subCatRB;
    private RadioButton categoriesRB;
    private ImageView toggleChartType;
    private ImageView showItems;
    private Button confirmBtn;
    private LinearLayout itemListForAnalysis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_expenses);

        initializeUiElements();
        setupClickListeners();
    }

    private void initializeUiElements() {
        barChart = findViewById(R.id.barChart);
        toggleChartType = findViewById(R.id.toggleChartType);
        showItems = findViewById(R.id.showItems);
        itemListForAnalysis = findViewById(R.id.item_list_for_analyse);
        itemRadioGroup = findViewById(R.id.itemRadioGroup);
        categoriesRB = findViewById(R.id.categoriesRB);
        subCatRB = findViewById(R.id.subCatRB);
        expenseRB = findViewById(R.id.expenseRB);
        confirmBtn = findViewById(R.id.confirmBtn);
        inflateBarChart();
        selectedItemType = GenericItemType.Category;
        categoriesRB.setChecked(true);
        currentChartType = BAR_CHART;
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

        showItems.setOnClickListener(v -> {
            toggleItemList();
        });

        itemRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {

            }
        });

        confirmBtn.setOnClickListener(v -> {
            if (categoriesRB.getId() == itemRadioGroup.getCheckedRadioButtonId()) {
                selectedItemType = GenericItemType.Category;
            } else if (subCatRB.getId() == itemRadioGroup.getCheckedRadioButtonId()) {
                selectedItemType = GenericItemType.Sub_Category;
            } else {
                selectedItemType = GenericItemType.Expenses;
            }
            toggleItemList();
//            fetch the selected list type for xy coordinates than set the new xy coordinates for the chat to show and rcv
        });
    }

    private void toggleItemList() {
        if (itemListForAnalysis.getVisibility() == View.GONE) {
            itemListForAnalysis.setVisibility(View.VISIBLE);
            itemListForAnalysis.setTranslationY(-10);
            itemListForAnalysis.animate().translationY(0).setInterpolator(new LinearInterpolator()).setDuration(100).start();
            showItems.animate().rotation(180).setInterpolator(new LinearInterpolator()).setDuration(200).start();
        } else {
            itemListForAnalysis.animate().translationY(-10).setInterpolator(new LinearInterpolator()).setDuration(100).withEndAction(() -> {
                itemListForAnalysis.setVisibility(View.GONE);
                itemListForAnalysis.setTranslationY(0);
            });
            showItems.animate().rotation(0).setInterpolator(new LinearInterpolator()).setDuration(200).start();
        }
    }

    private void inflateBarChart() {
        List<Integer> y = Arrays.asList(10, 15, 7, 20, 13, 22, 5, 1, 6, 7, 8, 16);

        List<BarEntry> co_ordinates = new ArrayList<>();
        for (int i = 0; i < y.size(); i++) {
            co_ordinates.add(new BarEntry(i, y.get(i)));
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
        setupBarChart();
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void setupBarChart() {
        Description desc = new Description();
        desc.setEnabled(false);
        //x axis of the bar chart
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
//        xAxis.setLabelRotationAngle(90f);
        xAxis.setTextColor(getColor(R.color.primaryTextColor));
        String[] xAxisLabels = Utility_Calendar.getMonths();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        //left y axis of bar chart
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(getColor(R.color.primaryTextColor));
        //right y axis of bar chart
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setTextColor(getColor(R.color.primaryTextColor));
        //legend color is the label's legend that should match with the bar color
        Legend legend = barChart.getLegend();
        legend.setTextColor(getColor(R.color.primaryTextColor));
        //setting up some bar
        barChart.setNoDataText("No expenses to show");
        barChart.setFitBars(true);
        barChart.setPinchZoom(true);
        barChart.setVisibleXRangeMaximum(20f);
        barChart.setDescription(desc);
        barChart.setDragEnabled(true);
//        barChart.setExtraTopOffset(20f);
        barChart.setScaleEnabled(true);
        barChart.setHighlightPerTapEnabled(false);
        barChart.setNoDataTextColor(getColor(R.color.primaryTextColor));
        barChart.setBorderColor(getColor(R.color.secondaryColor));
    }
}