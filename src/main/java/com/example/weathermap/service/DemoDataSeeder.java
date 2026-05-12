package com.example.weathermap.service;

import com.example.weathermap.domain.DistrictWeatherSnapshot;
import com.example.weathermap.repository.DistrictWeatherSnapshotRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true", matchIfMissing = true)
public class DemoDataSeeder {

    private final DistrictWeatherSnapshotRepository repository;

    public DemoDataSeeder(DistrictWeatherSnapshotRepository repository) {
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedIfEmpty() {
        if (repository.count() > 0) {
            return;
        }

        List<String> districtCodes = List.of("D-NORTH", "D-WEST", "D-CENTRAL", "D-EAST", "D-SOUTH");
        List<String> districtNames = List.of("North District", "West District", "Central District", "East District", "South District");
        Instant now = Instant.now();

        for (int dayOffset = 0; dayOffset < 5; dayOffset++) {
            LocalDate date = LocalDate.now().minusDays(dayOffset);
            for (int i = 0; i < districtCodes.size(); i++) {
                double temp = 22 + i + (dayOffset * 0.6);
                double humidity = 50 + i * 4 + dayOffset;
                repository.save(DistrictWeatherSnapshot.of(
                        districtCodes.get(i),
                        districtNames.get(i),
                        date,
                        temp,
                        humidity,
                        dayOffset == 0 ? "Partly Cloudy" : "Cloudy",
                        dayOffset == 0 ? "Light drizzle likely in next 2h" : "Stable conditions expected",
                        now
                ));
            }
        }
    }
}
