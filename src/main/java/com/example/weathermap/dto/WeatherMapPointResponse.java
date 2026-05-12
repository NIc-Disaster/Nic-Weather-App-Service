package com.example.weathermap.dto;

import java.time.LocalDate;

public record WeatherMapPointResponse(
        String districtCode,
        String districtName,
        LocalDate observationDate,
        Double temperatureCelsius,
        Double humidityPercent,
        String weatherCondition,
        String nowcastSummary
) {
}
