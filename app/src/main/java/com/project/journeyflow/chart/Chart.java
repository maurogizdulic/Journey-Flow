package com.project.journeyflow.chart;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chart {

    public static void showLineChartDistance(LineChart chart, List<Date> xAxisList, List<Double> yAxisList) {

        ArrayList<Entry> entries = new ArrayList<>();

        if (xAxisList.size() == yAxisList.size()) {
            int index = 0;
            for (Double value : yAxisList) {
                entries.add(new Entry(index, value.floatValue()));
                index++;
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Distance in meters");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.getXAxis().setTextSize(12f); // Adjust text size
        chart.getXAxis().setValueFormatter(new DateFormatter(xAxisList));
        chart.getXAxis().setLabelRotationAngle(-45);
        chart.getXAxis().setGranularity(1f); // Ensure one label per point
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setDrawAxisLine(true);
        chart.getXAxis().setDrawGridLines(false);

        // Add animations
        chart.animateX(1000);
        chart.animateY(1000);

        // Add description
        chart.getDescription().setText("Distance chart");

        // Refresh the chart
        chart.invalidate();
    }

    public static void showLineChartAltitude(LineChart chart, List<Double> xAxisList, List<Double> yAxisList) {
        ArrayList<Entry> entries = new ArrayList<>();

        if (xAxisList.size() == yAxisList.size()) {
            int index = 0;
            for (Double value : yAxisList) {
                entries.add(new Entry(xAxisList.get(index).floatValue(), value.floatValue()));
                index++;
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Distance in meters");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawFilled(true);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.getXAxis().setTextSize(12f); // Adjust text size
        //chart.getXAxis().setValueFormatter(new DateFormatter(yAxisList));
        chart.getXAxis().setLabelRotationAngle(-45);
        chart.getXAxis().setGranularity(1f); // Ensure one label per point
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setDrawAxisLine(true);
        chart.getXAxis().setDrawGridLines(false);

        // Add animations
        chart.animateX(1000);
        chart.animateY(1000);

        // Add description
        chart.getDescription().setText("Altitude chart");

        // Refresh the chart
        chart.invalidate();
    }

    public static void showLineChartSpeed(LineChart chart, List<Double> xAxisList, List<Float> yAxisList) {
        ArrayList<Entry> entries = new ArrayList<>();

        if (xAxisList.size() == yAxisList.size()) {
            int index = 0;
            for (Float value : yAxisList) {
                entries.add(new Entry(xAxisList.get(index).floatValue(), value));
                index++;
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Distance in meters");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setDrawFilled(true);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        chart.getXAxis().setTextSize(12f); // Adjust text size
        //chart.getXAxis().setValueFormatter(new DateFormatter(yAxisList));
        chart.getXAxis().setLabelRotationAngle(-45);
        chart.getXAxis().setGranularity(1f); // Ensure one label per point
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setDrawAxisLine(true);
        chart.getXAxis().setDrawGridLines(false);

        // Add animations
        chart.animateX(1000);
        chart.animateY(1000);

        // Add description
        chart.getDescription().setText("Speed chart");

        // Refresh the chart
        chart.invalidate();
    }


}
