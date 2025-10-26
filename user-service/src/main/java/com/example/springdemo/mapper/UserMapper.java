package com.example.springdemo.mapper;

import com.example.springdemo.controller.UserController;
import com.example.springdemo.dto.UserResponseDto;
import com.example.springdemo.dto.CreateUserDto;
import com.example.springdemo.entity.User;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Component
public class UserMapper {  // ← Добавь class

    public UserResponseDto toDto(User user) {
        UserResponseDto dto = new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getAge()
        );

        // HATEOAS ссылки
        dto.add(linkTo(methodOn(UserController.class)
                .getUser(user.getId())).withSelfRel());
        dto.add(linkTo(methodOn(UserController.class)
                .updateUser(user.getId(), null)).withRel("update"));
        dto.add(linkTo(methodOn(UserController.class)
                .deleteUser(user.getId())).withRel("delete"));

        return dto;
    }

    public User toEntity(CreateUserDto dto) {
        User user = new User();
        user.setName(dto.name());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setAge(dto.age());
        return user;
    }
}