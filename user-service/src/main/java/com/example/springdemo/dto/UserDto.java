package com.example.springdemo.dto;

import java.time.Instant;

public record UserDto(
        String name,
        String lastName,
        String email,
        Integer age
) {}
