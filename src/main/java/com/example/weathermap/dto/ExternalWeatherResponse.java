package com.example.weathermap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record ExternalWeatherResponse(
        @JsonProperty("generated_at") Instant generatedAt,
        List<DistrictWeatherPayload> districts
) {

    public record DistrictWeatherPayload(
            @JsonProperty("district_code") String districtCode,
            @JsonProperty("district_name") String districtName,
            @JsonProperty("observation_date") LocalDate observationDate,
            @JsonProperty("temperature_celsius") Double temperatureCelsius,
            @JsonProperty("humidity_percent") Double humidityPercent,
            @JsonProperty("weather_condition") String weatherCondition,
            @JsonProperty("nowcast_summary") String nowcastSummary
    ) {
    }
}
