package com.example.springdemo.controller;


import com.example.springdemo.dto.UserDto;
import com.example.springdemo.dto.UserRegistrationDto;
import com.example.springdemo.entity.User;
import com.example.springdemo.mapper.UserMapper;
import com.example.springdemo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;


import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @Test
    void shouldReturnAllUsers() throws Exception {
        // Сущность из БД
        User user = User.builder()
                .id(1L)
                .name("Artur")
                .lastName("Marchenko")
                .email("a@mail.com")
                .age(20)
                .build();

        // DTO, которое контроллер вернёт клиенту
        UserDto dto = new UserDto(
                "Artur", "Marchenko", "a@mail.com", 20
        );

        // Настраиваем поведение моков
        when(userService.getAllUsers()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        // Вызываем контроллер
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("a@mail.com"))
                .andExpect(jsonPath("$[0].name").value("Artur"));
    }

    @Test
    void shouldCreateUser() throws Exception {
        // given: входящий DTO запроса (то, что шлёт клиент)
        UserRegistrationDto req = new UserRegistrationDto(
                "Artur",
                "Marchenko",
                "a@mail.com",
                20,
                "12345678"

        );

        // сущность, которую сервис примет на вход
        User entityToSave = User.builder()
                .name("Artur")
                .lastName("Marchenko")
                .email("a@mail.com")
                .password("12345678")
                .age(20)
                .build();

        // сущность, которую "сохранит" сервис и вернёт
        User saved = User.builder()
                .id(1L)
                .name("Artur")
                .lastName("Marchenko")
                .email("a@mail.com")
                .age(20)
                .build();

        // DTO ответа (что вернёт контроллер клиенту)
        UserDto resp = new UserDto(
                "Artur", "Marchenko", "a@mail.com", 20
        );

        // маппинги и сервис
        when(userMapper.toEntity(req)).thenReturn(entityToSave);
        when(userService.createUser(entityToSave)).thenReturn(saved);
        when(userMapper.toDto(saved)).thenReturn(resp);

        // when + then
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))) // никаких текст-блоков
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("a@mail.com"))
                .andExpect(jsonPath("$.name").value("Artur"));
    }
}



