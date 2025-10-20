package com.example.springdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

// DTO для входящих данных от клиента, прописываем критерии входящих данных
public record UserRegistrationDto(
        @NotBlank @Size(max = 50) String name,
        @NotBlank @Size(max = 50) String lastName,
        @NotBlank @Email @Size(max = 254) String email,
        @JsonProperty(required = false) @Min(0) @Max(150) Integer age,
        @NotBlank @Size(min = 6, max = 72) String password
) {}
