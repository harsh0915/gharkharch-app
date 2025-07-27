package com.itmart.gharkharch.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.itmart.gharkharch.R;

import java.util.ArrayList;
import java.util.List;

public class AnalyseExpensesActivity extends AppCompatActivity {

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_expenses);

        initializeUiElements();
        setupClickListeners();
    }

    private void initializeUiElements() {
        barChart = findViewById(R.id.pieChart);
        setupBarChart();
    }

    private void setupBarChart() {
        int[] dataObjects = {55, 66, 77, 88, 99, 112, 423, 775};
        int[] dataObjects2 = {255, 366, 577, 588, 699, 912, 423, 175};
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < dataObjects2.length; i++) {
            // turn your data into Entry objects
            entries.add(new BarEntry(dataObjects[i], dataObjects2[i]));
        }
        BarDataSet barDataSet = new BarDataSet(entries, "Label"); // add entries to dataset
        barDataSet.setColor(R.color.warning_yellow);
//        dataSet.setValueTextColor(...); // styling, .
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(25f);
        barChart.setNoDataText("No expenses to show");
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(false);
        Description desc= new Description();
        desc.setText("");
        barChart.setDescription(desc);
        barChart.invalidate(); // refresh
    }

    private void setupClickListeners() {
    }
}