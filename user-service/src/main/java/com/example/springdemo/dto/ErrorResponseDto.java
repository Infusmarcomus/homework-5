package com.example.springdemo.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Модель ошибки API")
public class ErrorResponseDto {

    @Schema(description = "Код ошибки", example = "USER_NOT_FOUND")
    private String error;

    @Schema(description = "Сообщение об ошибке", example = "Пользователь с ID 999 не найден")
    private String message;

    @Schema(description = "Время возникновения ошибки", example = "2023-10-25 19:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP статус код", example = "404")
    private int status;

    @Schema(description = "HTTP статус текст", example = "Not Found")
    private String statusText;

    @Schema(description = "Путь запроса", example = "/api/users/999")
    private String path;

    // Конструкторы
    public ErrorResponseDto() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDto(String error, String message) {
        this();
        this.error = error;
        this.message = message;
    }

    public ErrorResponseDto(String error, String message, int status, String statusText) {
        this();
        this.error = error;
        this.message = message;
        this.status = status;
        this.statusText = statusText;
    }

    public ErrorResponseDto(String error, String message, int status, String statusText, String path) {
        this();
        this.error = error;
        this.message = message;
        this.status = status;
        this.statusText = statusText;
        this.path = path;
    }

    // Геттеры и сеттеры
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                ", statusText='" + statusText + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}