package com.example.weathermap.service;

import com.example.weathermap.domain.DistrictWeatherSnapshot;
import com.example.weathermap.dto.DistrictHistoryResponse;
import com.example.weathermap.dto.WeatherMapPointResponse;
import com.example.weathermap.repository.DistrictWeatherSnapshotRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class WeatherQueryService {

    private final DistrictWeatherSnapshotRepository snapshotRepository;

    public WeatherQueryService(DistrictWeatherSnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }

    public List<WeatherMapPointResponse> getMapDataForLatestDate() {
        LocalDate latestDate = snapshotRepository.findTopByOrderByObservationDateDesc()
                .map(DistrictWeatherSnapshot::getObservationDate)
                .orElse(LocalDate.now());
        List<DistrictWeatherSnapshot> snapshots = snapshotRepository.findByObservationDate(latestDate);

        return snapshots.stream()
                .map(this::toMapPoint)
                .sorted(Comparator.comparing(WeatherMapPointResponse::districtName))
                .toList();
    }

    public DistrictHistoryResponse getDistrictHistory(String districtCode, int days) {
        int normalizedDays = Math.max(1, Math.min(days, 5));
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(normalizedDays - 1L);

        List<DistrictWeatherSnapshot> snapshots = snapshotRepository
                .findByDistrictCodeAndObservationDateBetweenOrderByObservationDateDesc(districtCode, startDate, endDate);

        String districtName = snapshots.stream()
                .findFirst()
                .map(DistrictWeatherSnapshot::getDistrictName)
                .orElse(districtCode);

        List<DistrictHistoryResponse.HistoryPoint> points = snapshots.stream()
                .map(snapshot -> new DistrictHistoryResponse.HistoryPoint(
                        snapshot.getObservationDate().toString(),
                        snapshot.getTemperatureCelsius(),
                        snapshot.getHumidityPercent(),
                        snapshot.getWeatherCondition(),
                        snapshot.getNowcastSummary()
                ))
                .toList();

        return new DistrictHistoryResponse(districtCode, districtName, normalizedDays, points);
    }

    public Map<String, String> getDistrictOptions() {
        return getMapDataForLatestDate().stream()
                .collect(Collectors.toMap(
                        WeatherMapPointResponse::districtCode,
                        WeatherMapPointResponse::districtName,
                        (left, right) -> left
                ));
    }

    private WeatherMapPointResponse toMapPoint(DistrictWeatherSnapshot snapshot) {
        return new WeatherMapPointResponse(
                snapshot.getDistrictCode(),
                snapshot.getDistrictName(),
                snapshot.getObservationDate(),
                snapshot.getTemperatureCelsius(),
                snapshot.getHumidityPercent(),
                snapshot.getWeatherCondition(),
                snapshot.getNowcastSummary()
        );
    }
}
