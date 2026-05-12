package com.example.weathermap.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "district_weather_snapshot",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_district_date",
                columnNames = {"district_code", "observation_date"}
        )
)
public class DistrictWeatherSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "district_code", nullable = false, length = 32)
    private String districtCode;

    @Column(name = "district_name", nullable = false, length = 128)
    private String districtName;

    @Column(name = "observation_date", nullable = false)
    private LocalDate observationDate;

    @Column(nullable = false)
    private Double temperatureCelsius;

    @Column(nullable = false)
    private Double humidityPercent;

    @Column(nullable = false, length = 128)
    private String weatherCondition;

    @Column(nullable = false, length = 255)
    private String nowcastSummary;

    @Column(nullable = false)
    private Instant sourceUpdatedAt;

    @Column(nullable = false)
    private Instant createdAt;

    public static DistrictWeatherSnapshot of(
            String districtCode,
            String districtName,
            LocalDate observationDate,
            Double temperatureCelsius,
            Double humidityPercent,
            String weatherCondition,
            String nowcastSummary,
            Instant sourceUpdatedAt
    ) {
        DistrictWeatherSnapshot snapshot = new DistrictWeatherSnapshot();
        snapshot.districtCode = districtCode;
        snapshot.districtName = districtName;
        snapshot.observationDate = observationDate;
        snapshot.temperatureCelsius = temperatureCelsius;
        snapshot.humidityPercent = humidityPercent;
        snapshot.weatherCondition = weatherCondition;
        snapshot.nowcastSummary = nowcastSummary;
        snapshot.sourceUpdatedAt = sourceUpdatedAt;
        snapshot.createdAt = Instant.now();
        return snapshot;
    }

    public Long getId() {
        return id;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public LocalDate getObservationDate() {
        return observationDate;
    }

    public Double getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public Double getHumidityPercent() {
        return humidityPercent;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public String getNowcastSummary() {
        return nowcastSummary;
    }

    public Instant getSourceUpdatedAt() {
        return sourceUpdatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void refreshWeatherData(
            String districtName,
            Double temperatureCelsius,
            Double humidityPercent,
            String weatherCondition,
            String nowcastSummary,
            Instant sourceUpdatedAt
    ) {
        this.districtName = districtName;
        this.temperatureCelsius = temperatureCelsius;
        this.humidityPercent = humidityPercent;
        this.weatherCondition = weatherCondition;
        this.nowcastSummary = nowcastSummary;
        this.sourceUpdatedAt = sourceUpdatedAt;
    }
}
