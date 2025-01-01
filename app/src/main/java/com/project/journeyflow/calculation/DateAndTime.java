package com.project.journeyflow.calculation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

public class DateAndTime {

    public static Date getCurrentDateTimeStart() {
        LocalDate today = LocalDate.now();
        return Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getCurrentDateTimeEnd() {
        LocalDate today = LocalDate.now();
        return Date.from(today.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getStartOfMonth() {
        LocalDateTime inputDate = LocalDateTime.now();
        // Get the start date of the month
        LocalDateTime startOfMonth = inputDate.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return Date.from(startOfMonth.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getEndOfMonth() {
        LocalDateTime inputDate = LocalDateTime.now();
        LocalDateTime endOfMonth = inputDate.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999_999_999);
        return Date.from(endOfMonth.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getStartOfCurrentYear() {
        int currentYear = LocalDateTime.now().getYear();

        // Start of the year
        LocalDateTime startOfYear = LocalDateTime.of(currentYear, 1, 1, 0, 0, 0);
        return Date.from(startOfYear.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getEndOfCurrentYear() {
        int currentYear = LocalDateTime.now().getYear();

        // End of the year
        LocalDateTime endOfYear = LocalDateTime.of(currentYear, 12, 31, 23, 59, 59);
        return Date.from(endOfYear.atZone(ZoneId.systemDefault()).toInstant());
    }

}
