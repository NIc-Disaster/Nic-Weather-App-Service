package com.example.weathermap.controller;

import com.example.weathermap.dto.DistrictHistoryResponse;
import com.example.weathermap.dto.WeatherMapPointResponse;
import com.example.weathermap.service.WeatherQueryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/api/weather")
public class WeatherApiController {

    private final WeatherQueryService weatherQueryService;

    public WeatherApiController(WeatherQueryService weatherQueryService) {
        this.weatherQueryService = weatherQueryService;
    }

    @GetMapping("/map")
    public List<WeatherMapPointResponse> getMapData() {
        return weatherQueryService.getMapDataForLatestDate();
    }

    @GetMapping("/district/{districtCode}/history")
    public DistrictHistoryResponse getDistrictHistory(
            @PathVariable String districtCode,
            @RequestParam(defaultValue = "5") @Min(1) @Max(5) int days
    ) {
        return weatherQueryService.getDistrictHistory(districtCode, days);
    }
}
