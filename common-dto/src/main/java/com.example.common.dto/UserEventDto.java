package com.example.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.time.Instant;

public record UserEventDto(
        @JsonProperty("eventType")
        EventType eventType,  // ← enum вместо String

        @JsonProperty("email")
        String email,

        @JsonProperty("timestamp")
        Instant timestamp
) {

    public enum EventType {
        USER_CREATED,
        USER_UPDATED,
        USER_DELETED
    }

    // Фабричные методы
    public static UserEventDto created(String email) {
        return new UserEventDto(EventType.USER_CREATED, email, Instant.now());
    }

    public static UserEventDto updated(String email) {
        return new UserEventDto(EventType.USER_UPDATED, email, Instant.now());
    }

    public static UserEventDto deleted(String email) {
        return new UserEventDto(EventType.USER_DELETED, email, Instant.now());
    }
}