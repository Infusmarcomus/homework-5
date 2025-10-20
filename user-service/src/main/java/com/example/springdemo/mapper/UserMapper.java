package com.example.springdemo.mapper;

import com.example.springdemo.dto.UserDto;
import com.example.springdemo.dto.UserRegistrationDto;
import com.example.springdemo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User u) {
        return new UserDto(
                u.getName(),
                u.getLastName(),
                u.getEmail(),
                u.getAge()
        );
    }

    public User toEntity(UserDto dto) {
        User user = new User();
        user.setName(dto.name());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setAge(dto.age());
        return user;
    }

    // 👇 новый метод специально для регистрации
    public User toEntity(UserRegistrationDto dto) {
        User user = new User();
        user.setName(dto.name());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setAge(dto.age());
        user.setPassword(dto.password());
        return user;
    }
}

