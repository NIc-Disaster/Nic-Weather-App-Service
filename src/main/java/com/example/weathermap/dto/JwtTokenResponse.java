package com.example.weathermap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") Long expiresInSeconds
) {
}
