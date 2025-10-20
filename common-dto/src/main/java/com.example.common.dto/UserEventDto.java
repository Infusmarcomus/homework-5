package com.example.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public record UserEventDto(
        @JsonProperty("eventType")
        String eventType,

        @JsonProperty("userEmail")
        String userEmail,

        @JsonProperty("timestamp")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant timestamp
) {
    public static UserEventDto created(String email) {
        return new UserEventDto("USER_CREATED", email, Instant.now());
    }

    public static UserEventDto deleted(String email) {
        return new UserEventDto("USER_DELETED", email, Instant.now());
    }

}