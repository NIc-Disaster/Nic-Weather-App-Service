package com.example.weathermap.service;

import com.example.weathermap.config.WeatherApiProperties;
import com.example.weathermap.dto.ExternalWeatherResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternalWeatherClient {

    private final RestClient restClient;
    private final WeatherApiProperties properties;

    public ExternalWeatherClient(RestClient restClient, WeatherApiProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public ExternalWeatherResponse fetchDistrictWeather(String jwtToken) {
        ExternalWeatherResponse response = restClient.get()
                .uri(properties.weatherUrl())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .body(ExternalWeatherResponse.class);

        if (response == null || response.districts() == null) {
            throw new IllegalStateException("Weather API returned invalid response");
        }
        return response;
    }
}
