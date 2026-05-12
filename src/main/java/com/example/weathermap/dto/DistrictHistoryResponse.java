package com.example.weathermap.dto;

import java.util.List;

public record DistrictHistoryResponse(
        String districtCode,
        String districtName,
        int days,
        List<HistoryPoint> history
) {

    public record HistoryPoint(
            String date,
            Double temperatureCelsius,
            Double humidityPercent,
            String weatherCondition,
            String nowcastSummary
    ) {
    }
}
