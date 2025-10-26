package com.example.springdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateUserDto(
        @Schema(description = "Имя пользователя", example = "Артур")
        String name,

        @Schema(description = "Фамилия пользователя", example = "Марченко")
        String lastName,

        @Schema(description = "Email пользователя", example = "artur@mail.ru")
        String email,

        @Schema(description = "Возраст пользователя", example = "20")
        Integer age
){}
