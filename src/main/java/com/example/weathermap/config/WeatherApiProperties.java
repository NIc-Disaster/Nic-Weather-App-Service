package com.example.weathermap.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "weather.api")
public record WeatherApiProperties(
        @NotBlank String authUrl,
        @NotBlank String weatherUrl,
        @NotBlank String clientId,
        @NotBlank String clientSecret
) {
}
