package com.example.springdemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long id) {
        super("USER_NOT_FOUND", "Пользователь с ID " + id + " не найден");
    }

    public UserNotFoundException(String email) {
        super("USER_NOT_FOUND", "Пользователь с email " + email + " не найден");
    }
}