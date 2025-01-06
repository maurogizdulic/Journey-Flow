package com.project.journeyflow.chart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.project.journeyflow.R;
import com.project.journeyflow.query.profile.StatisticsQuery;

import java.util.List;

public class BarChartPagerAdapter extends RecyclerView.Adapter<BarChartPagerAdapter.BarChartViewHolder>{

    private final Context context;
    private final List<List<BarEntry>> chartDataList;
    private final String type;
    private StatisticsQuery statisticsQuery;

    public BarChartPagerAdapter(Context context, List<List<BarEntry>> chartDataList, String type) {
        this.context = context;
        this.chartDataList = chartDataList;
        this.type = type;
    }

    @NonNull
    @Override
    public BarChartPagerAdapter.BarChartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chart_layout, parent, false);
        statisticsQuery = new StatisticsQuery(context);
        return new BarChartViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BarChartPagerAdapter.BarChartViewHolder holder, int position) {
        // Set up bar chart data
        List<BarEntry> entries = chartDataList.get(position);
        BarDataSet dataSet = new BarDataSet(entries, "Dataset " + (position + 1));
        dataSet.setColor(context.getResources().getColor(android.R.color.holo_blue_light));
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.9f); // Set bar width

        final String[] dates = {"06.01.2025"};


        if (type.equals("MONTH")) {
            if (position == 0) {
                holder.textViewChartTitle.setText("Chart of number of journeys per day");
                holder.textViewXlabel.setText("Day");
                holder.textViewYlabel.setText("Number of journey");
            }
            else if (position == 1) {
                holder.textViewChartTitle.setText("Chart of journey duration per day");
                holder.textViewXlabel.setText("Day");
                holder.textViewYlabel.setText("Duration (min)");
            }
            else {
                holder.textViewChartTitle.setText("Chart of distances traveled per day");
                holder.textViewXlabel.setText("Day");
                holder.textViewYlabel.setText("Distance (km)");
            }
        } else if (type.equals("YEAR")) {
            holder.textViewMonthYear.setText("Year");
            holder.textViewMonthYearValue.setText("2025");

            if (position == 0) {
                holder.textViewChartTitle.setText("Chart of number of journeys per month");
                holder.textViewXlabel.setText("Month");
                holder.textViewYlabel.setText("Number of journey");
            }
            else if (position == 1) {
                holder.textViewChartTitle.setText("Chart of journey duration per month");
                holder.textViewXlabel.setText("Month");
                holder.textViewYlabel.setText("Duration (min)");
            }
            else {
                holder.textViewChartTitle.setText("Chart of distances traveled per month");
                holder.textViewXlabel.setText("Month");
                holder.textViewYlabel.setText("Distance (km)");
            }
        }

        holder.barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dates));
        holder.barChart.getXAxis().setGranularity(1f); // Interval of 1
        holder.barChart.getXAxis().setGranularityEnabled(true);
        holder.barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        holder.barChart.setData(barData);
        holder.barChart.setFitBars(true); // Fit bars to the chart
        holder.barChart.invalidate(); // Refresh chart
    }

    @Override
    public int getItemCount() {
        return chartDataList.size();
    }

    public static class BarChartViewHolder extends RecyclerView.ViewHolder {
        BarChart barChart;
        TextView textViewChartTitle, textViewXlabel, textViewYlabel, textViewMonthYear, textViewMonthYearValue;

        public BarChartViewHolder(@NonNull View itemView) {
            super(itemView);
            barChart = itemView.findViewById(R.id.barChart);
            textViewChartTitle = itemView.findViewById(R.id.textViewStatisticsTitleChart);
            textViewXlabel = itemView.findViewById(R.id.textViewStatisticsChartXlabel);
            textViewYlabel = itemView.findViewById(R.id.textViewStatisticsChartYlabel);
            textViewMonthYear = itemView.findViewById(R.id.textViewStatisticsMonthYear);
            textViewMonthYearValue = itemView.findViewById(R.id.textViewStatisticsMonthYearValue);
        }
    }
}
