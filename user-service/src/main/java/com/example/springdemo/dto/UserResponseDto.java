package com.example.springdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;

//отдаем клиенту для ответа
public class UserResponseDto extends RepresentationModel<UserResponseDto> {

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Имя пользователя", example = "Артур")
    private String name;

    @Schema(description = "Фамилия пользователя", example = "Марченко")
    private String lastName;

    @Schema(description = "Email адрес", example = "artur@mail.ru")
    private String email;

    @Schema(description = "Возраст", example = "25", minimum = "18", maximum = "120")
    private Integer age;

    // Конструкторы
    public UserResponseDto() {}

    public UserResponseDto(Long id, String name, String lastName, String email, Integer age) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}