package com.example.weathermap.repository;

import com.example.weathermap.domain.DistrictWeatherSnapshot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistrictWeatherSnapshotRepository extends JpaRepository<DistrictWeatherSnapshot, Long> {

    Optional<DistrictWeatherSnapshot> findByDistrictCodeAndObservationDate(String districtCode, LocalDate observationDate);

    List<DistrictWeatherSnapshot> findByObservationDate(LocalDate observationDate);

    Optional<DistrictWeatherSnapshot> findTopByOrderByObservationDateDesc();

    List<DistrictWeatherSnapshot> findByDistrictCodeAndObservationDateBetweenOrderByObservationDateDesc(
            String districtCode,
            LocalDate fromDate,
            LocalDate toDate
    );
}
