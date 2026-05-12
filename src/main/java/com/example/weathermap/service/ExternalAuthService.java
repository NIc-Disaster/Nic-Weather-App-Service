package com.example.weathermap.service;

import com.example.weathermap.config.WeatherApiProperties;
import com.example.weathermap.dto.JwtTokenResponse;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExternalAuthService {

    private final RestClient restClient;
    private final WeatherApiProperties properties;

    public ExternalAuthService(RestClient restClient, WeatherApiProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    public String fetchJwtToken() {
        JwtTokenResponse tokenResponse = restClient.post()
                .uri(properties.authUrl())
                .body(Map.of(
                        "client_id", properties.clientId(),
                        "client_secret", properties.clientSecret()
                ))
                .retrieve()
                .body(JwtTokenResponse.class);

        if (tokenResponse == null || tokenResponse.accessToken() == null || tokenResponse.accessToken().isBlank()) {
            throw new IllegalStateException("JWT token API returned an empty token");
        }
        return tokenResponse.accessToken();
    }
}
