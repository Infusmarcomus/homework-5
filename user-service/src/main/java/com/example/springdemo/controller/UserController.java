package com.example.springdemo.controller;
import com.example.springdemo.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.springdemo.dto.UserDto;
import com.example.springdemo.dto.UserRegistrationDto;
import com.example.springdemo.entity.User;
import com.example.springdemo.service.UserService;
import java.util.List;
import java.util.Map;

@RestController // класс обрабатывает HTTP и возвращает JSON
@RequestMapping("/api/users") // базовый URL для всех методов
@RequiredArgsConstructor // позволяет не писать аргументы для конструктора
//класс; точка входа сюда приходит HTTP запросы post get delete контролер вызывает нужный метод сервиса и возвращает результат клиенту в JSON

public class UserController {

    private final UserService userService; //вот здесь
    private final UserMapper userMapper;

    // POST /api/users/register — регистрация (201 Created)
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED) // явный статус 201 для регистраци
    public UserDto register(@Valid @RequestBody UserRegistrationDto dto) { //валидируем входной JSON по аннотациям из UserRegistrationDto
        User user = userMapper.toEntity(dto);
        User created = userService.createUser(user);
        return userMapper.toDto(created);
    }

// GET /api/users — список всех
@GetMapping
public ResponseEntity<List<UserDto>> getAll() {
    List<UserDto> users = userService.getAllUsers()
            .stream()
            .map(userMapper::toDto)
            .toList();
    if (users.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(users);
}
// GET /api/users - получить по id
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(userMapper.toDto(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

// GET /api/users/by-email?email=...
    @GetMapping("/by-email")
    public ResponseEntity<?> getByEmail(@RequestParam("email") String email) {

        try {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(userMapper.toDto(user));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    // UPDATE /api/users/{id} - обновить
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id,
                                              @RequestBody UserDto userDto) {
        User updated = userService.updateUser(id, userMapper.toEntity(userDto));
        return ResponseEntity.ok(userMapper.toDto(updated));
    }

    // DELETE /api/users/{id} — удалить (204 No Content)
@DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
    }

}
