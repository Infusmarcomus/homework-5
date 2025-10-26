package com.example.springdemo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyDeletedException extends BusinessException {
    public UserAlreadyDeletedException(Long id) {
        super("USER_ALREADY_DELETED", "Пользователь с ID " + id + " уже удален");
    }
}