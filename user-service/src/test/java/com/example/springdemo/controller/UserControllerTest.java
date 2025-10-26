package com.example.springdemo.controller;

import com.example.springdemo.dto.CreateUserDto;
import com.example.springdemo.dto.UpdateUserDto;
import com.example.springdemo.dto.UserResponseDto;
import com.example.springdemo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    // Вспомогательный метод для создания UserResponseDto со ссылками
    private UserResponseDto createUserResponseDtoWithLinks(Long id, String name, String lastName, String email, Integer age) {
        UserResponseDto dto = new UserResponseDto(id, name, lastName, email, age);
        // Добавляем HATEOAS ссылки как в реальном Mapper'е
        dto.add(Link.of("http://localhost/api/users/" + id, "self"));
        dto.add(Link.of("http://localhost/api/users/" + id, "update"));
        dto.add(Link.of("http://localhost/api/users/" + id, "delete"));
        return dto;
    }

    // Тест 1: Успешное создание пользователя с HATEOAS
    @Test
    void createUser_ShouldReturn201WithHateoas() throws Exception {
        // Given
        CreateUserDto createDto = new CreateUserDto("Артур", "Марченко", "artur@mail.ru", 25, "123456");
        UserResponseDto responseDto = createUserResponseDtoWithLinks(1L, "Артур", "Марченко", "artur@mail.ru", 25);

        when(userService.createUser(any(CreateUserDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Артур"))
                .andExpect(jsonPath("$.lastName").value("Марченко"))
                .andExpect(jsonPath("$.email").value("artur@mail.ru"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.update.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());
    }

    // Тест 2: Получение пользователя по ID с HATEOAS
    @Test
    void getUser_ShouldReturn200WithHateoas() throws Exception {
        // Given
        Long userId = 1L;
        UserResponseDto responseDto = createUserResponseDtoWithLinks(1L, "Артур", "Марченко", "artur@mail.ru", 25);

        when(userService.getUserById(userId)).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Артур"))
                .andExpect(jsonPath("$.lastName").value("Марченко"))
                .andExpect(jsonPath("$.email").value("artur@mail.ru"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.update.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());
    }

    // Тест 3: Получение пользователя по email с HATEOAS
    @Test
    void getByEmail_ShouldReturn200WithHateoas() throws Exception {
        // Given
        String email = "artur@mail.ru";
        UserResponseDto responseDto = createUserResponseDtoWithLinks(1L, "Артур", "Марченко", "artur@mail.ru", 25);

        when(userService.getUserByEmail(email)).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(get("/api/users/by-email")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Артур"))
                .andExpect(jsonPath("$.lastName").value("Марченко"))
                .andExpect(jsonPath("$.email").value("artur@mail.ru"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.update.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());
    }

    // Тест 4: Обновление пользователя с HATEOAS
    @Test
    void updateUser_ShouldReturn200WithHateoas() throws Exception {
        // Given
        Long userId = 1L;
        UpdateUserDto updateDto = new UpdateUserDto("Артур", "Дмитриев", "artur.dmitriev@mail.ru", 30);
        UserResponseDto responseDto = createUserResponseDtoWithLinks(1L, "Артур", "Дмитриев", "artur.dmitriev@mail.ru", 30);

        when(userService.updateUser(eq(userId), any(UpdateUserDto.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Артур"))
                .andExpect(jsonPath("$.lastName").value("Дмитриев"))
                .andExpect(jsonPath("$.email").value("artur.dmitriev@mail.ru"))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.update.href").exists())
                .andExpect(jsonPath("$._links.delete.href").exists());
    }

    // Тест 5: Удаление пользователя
    @Test
    void deleteUser_ShouldReturn204() throws Exception {
        // Given
        Long userId = 1L;

        // When & Then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    // Тест 6: Получение всех пользователей с HATEOAS
    @Test
    void getAllUsers_ShouldReturn200WithHateoas() throws Exception {
        // Given
        UserResponseDto user1 = createUserResponseDtoWithLinks(1L, "Артур", "Марченко", "artur@mail.ru", 25);
        UserResponseDto user2 = createUserResponseDtoWithLinks(2L, "Мария", "Иванова", "maria@mail.ru", 30);

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Артур"))
                .andExpect(jsonPath("$[0].lastName").value("Марченко"))
                .andExpect(jsonPath("$[0].email").value("artur@mail.ru"))
                .andExpect(jsonPath("$[0].age").value(25))
                .andExpect(jsonPath("$[0].links[?(@.rel == 'self')].href").exists())
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Мария"))
                .andExpect(jsonPath("$[1].lastName").value("Иванова"))
                .andExpect(jsonPath("$[1].email").value("maria@mail.ru"))
                .andExpect(jsonPath("$[1].age").value(30))
                .andExpect(jsonPath("$[1].links[?(@.rel == 'self')].href").exists());
    }
}