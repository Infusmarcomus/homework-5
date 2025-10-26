package com.example.springdemo.controller;
import com.example.springdemo.dto.UpdateUserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.springdemo.dto.UserResponseDto;
import com.example.springdemo.dto.CreateUserDto;
import com.example.springdemo.service.UserService;
import java.util.List;


@RestController // класс обрабатывает HTTP и возвращает JSON
@RequestMapping("/api/users") // базовый URL для всех методов
@RequiredArgsConstructor // позволяет не писать аргументы для конструктора
//класс; точка входа сюда приходит HTTP запросы post get delete контролер вызывает нужный метод сервиса и возвращает результат клиенту в JSON

@Tag(name = "User API", description = "Операции с пользователями")



public class UserController {
    @Autowired
    private final UserService userService;

    // POST /api/users — регистрация (201 Created)
    @Operation(summary = "Регистрация пользователя",
    description = "Создает нового пользователя в системе",
    tags = {"User API"})

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // явный статус 201 для регистраци
    public UserResponseDto createUser(@Parameter(description = "регистрация пользователя")
                                        @Valid @RequestBody CreateUserDto createDto) {
        return userService.createUser(createDto);
    }

// GET /api/users — список всех
@GetMapping
@Operation(summary = "Получить всех пользователей")
public ResponseEntity<List<UserResponseDto>> getAllUsers() {
    List<UserResponseDto> userEntities = userService.getAllUsers();
    return ResponseEntity.ok(userEntities);
}

// GET /api/users/{id} - получить по id
    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@Parameter(description = "ID пользователя", example = "1")
                                                   @PathVariable("id") Long id) {
        UserResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

// GET /api/users/by-email?email=...
    @Operation(summary = "Получить пользователя по email")
    @GetMapping("/by-email")
    public ResponseEntity<UserResponseDto> getByEmail(@Parameter(description = "email пользователя")
                                            @RequestParam("email") String email) {
        UserResponseDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
        }

    // UPDATE /api/users/{id} - обновить
    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя по ID")
    public ResponseEntity<UserResponseDto> updateUser(@Parameter(description = "Обновить по ID")
                                                @PathVariable("id") Long id,
                                                      @RequestBody UpdateUserDto uptadeDto) {
        UserResponseDto updatedUser = userService.updateUser(id, uptadeDto);
        return ResponseEntity.ok(updatedUser);
    }

    // DELETE /api/users/{id} — удалить (204 No Content)
    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{id}")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
    }





