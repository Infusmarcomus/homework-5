package com.example.springdemo.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {
    private final String errorCode;

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }
}