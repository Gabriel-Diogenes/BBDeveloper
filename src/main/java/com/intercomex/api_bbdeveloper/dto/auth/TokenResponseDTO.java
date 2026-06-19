package com.intercomex.api_bbdeveloper.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenResponseDTO(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Integer expiresIn,
        String scope
) {

    @Override
    @JsonProperty("expires_in")
    public Integer expiresIn() {
        return expiresIn;
    }
}
