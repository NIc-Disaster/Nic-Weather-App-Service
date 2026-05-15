package com.example.weathermap.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class ForecastDayLabels {

    private static final DateTimeFormatter LABEL = DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH);

    private ForecastDayLabels() {
    }

    /** Today (day 0). */
    public static String today() {
        return LocalDate.now().format(LABEL);
    }

    /**
     * Forecast day offset from today. Day 1 = tomorrow (e.g. 4 May when today is 3 May).
     */
    public static String fromTodayPlusDays(int daysFromToday) {
        return LocalDate.now().plusDays(daysFromToday).format(LABEL);
    }
}
