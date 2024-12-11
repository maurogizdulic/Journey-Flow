package com.project.journeyflow.chart;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateFormatter extends ValueFormatter {

    private final List<Date> timeList;
    private final SimpleDateFormat dateFormat;

    public DateFormatter(List<Date> timeList) {
        this.timeList = timeList;
        this.dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    }

    @Override
    public String getFormattedValue(float value) {
        int index = Math.round(value);
        if (index >= 0 && index < timeList.size()) {
            return dateFormat.format(timeList.get(index));
        }
        return "";
    }
}
