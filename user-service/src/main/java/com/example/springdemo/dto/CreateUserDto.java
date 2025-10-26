package com.example.springdemo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

// DTO для входящих данных от клиента, прописываем критерии входящих данных
public record CreateUserDto(
        @NotBlank @Size(max = 50)
        @Schema(description = "Имя пользователя", example = "Артур")
        String name,

        @NotBlank @Size(max = 50)
        @Schema(description = "Фамилия пользователя", example = "Марченко")
        String lastName,

        @NotBlank @Email @Size(max = 254)
        @Schema(description = "Email пользователя", example = "artur@mail.ru")
        String email,

        @Schema(description = "Возраст пользователя", example = "20")
        @JsonProperty(required = false) @Min(0) @Max(150)
        Integer age,

        @NotBlank @Size(min = 6, max = 72)
        @Schema(description = "Пример пароля", example = "123456")
        @Size(min = 6, message = "Пароль должен быть минимум 6 символов")
        String password
) {}
