package com.example.weathermap.service;

import com.example.weathermap.domain.DistrictWeatherSnapshot;
import com.example.weathermap.dto.ExternalWeatherResponse;
import com.example.weathermap.repository.DistrictWeatherSnapshotRepository;
import java.time.Instant;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WeatherIngestionService {

    private static final Logger log = LoggerFactory.getLogger(WeatherIngestionService.class);

    private final ExternalAuthService externalAuthService;
    private final ExternalWeatherClient externalWeatherClient;
    private final DistrictWeatherSnapshotRepository snapshotRepository;

    public WeatherIngestionService(
            ExternalAuthService externalAuthService,
            ExternalWeatherClient externalWeatherClient,
            DistrictWeatherSnapshotRepository snapshotRepository
    ) {
        this.externalAuthService = externalAuthService;
        this.externalWeatherClient = externalWeatherClient;
        this.snapshotRepository = snapshotRepository;
    }

    @Transactional
    @Scheduled(cron = "${weather.scheduler.cron:0 0 1 * * *}")
    public void refreshDailyWeather() {
        log.info("Starting scheduled weather ingestion job");
        String jwtToken = externalAuthService.fetchJwtToken();
        ExternalWeatherResponse weatherResponse = externalWeatherClient.fetchDistrictWeather(jwtToken);
        Instant sourceUpdatedAt = weatherResponse.generatedAt() != null ? weatherResponse.generatedAt() : Instant.now();

        for (ExternalWeatherResponse.DistrictWeatherPayload district : weatherResponse.districts()) {
            LocalDate observationDate = district.observationDate() != null ? district.observationDate() : LocalDate.now();

            snapshotRepository.findByDistrictCodeAndObservationDate(district.districtCode(), observationDate)
                    .ifPresentOrElse(existing -> {
                        existing.refreshWeatherData(
                                district.districtName(),
                                district.temperatureCelsius(),
                                district.humidityPercent(),
                                district.weatherCondition(),
                                district.nowcastSummary(),
                                sourceUpdatedAt
                        );
                        snapshotRepository.save(existing);
                    }, () -> snapshotRepository.save(DistrictWeatherSnapshot.of(
                            district.districtCode(),
                            district.districtName(),
                            observationDate,
                            district.temperatureCelsius(),
                            district.humidityPercent(),
                            district.weatherCondition(),
                            district.nowcastSummary(),
                            sourceUpdatedAt
                    )));
        }
        log.info("Weather ingestion finished. Districts processed: {}", weatherResponse.districts().size());
    }
}
