package com.example.springdemo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.UpdateTimestamp;
import com.example.springdemo.model.enums.Role;

import java.time.Instant;


@Entity // этот класс — сущность, его объекты нужно связывать с записями в таблице БД
@Table( // используется вместе с entity как именно будет мапиться с таблицей в базе данных
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email", unique=true)
        }
) // index на таблице email позволяет искать по дереву быстрее O(log n), а не O(n), также добавил уникальность
@Getter //геттеры и сеттеры через ломбук чтобы вручную не прописывать
@Setter
@NoArgsConstructor // конструктор без аргументов (нужен Hibernate для инициализации)
@AllArgsConstructor // конструктор со всеми аргументами
@Builder //удобная аннотация для тестов


public class User {
    @Id // аннотация говорит hibernate это первичный ключ
    @GeneratedValue(strategy = GenerationType.IDENTITY) // говорит: БД будет генерировать айди автоматом
    // автоинкрементная стратегия POSTgres Serial/Bigserial, в дальнййшем можно будет попробовать UUID уник айди


    private Long id;

    @NotBlank //поле не может быть null, пустым или состоять только из пробелов работает только для String
    @Size(max = 50) // длина строки
    @Column(name = "first_name", nullable = false, length = 50) // как создавать столбец в таблица для hibernate


    @NotBlank(message = "Имя обязательно")
    private String name;

    @NotBlank(message = "Фамилия обязательна")
    @Size(max = 50)
    @Column(name = "last_name", nullable = false, length = 50)


    private String lastName;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, length = 254) // unique = true не пишу потому что хочу сделать контроль через table там уже есть параметр

    private String email;

    @JsonIgnore
    @NotBlank
    @Size(min = 60, max = 100)
    @Column(nullable = false, length = 100)
    private String password;

    @Min(1)
    @Max(150)
    @Column(nullable = true) // пусть возраст будет необязательным

    private Integer age;


    @CreationTimestamp // когда создаёшь новую запись (INSERT), автоматически проставь сюда текущее время
    @Column(nullable = false, updatable = false) // updatable false гарантирует что датавремя создания не изменится даже если попытаюсь вызвать user.setCreatedAt or save
    @Schema(description = "Дата создания", example = "2023-01-01T10:00:00")
    private Instant createdAt; // localdatetime хранит дату и время без часового пояса instant точное время по UTC

    @UpdateTimestamp // можно будет понять как ORM следит за изменениями обьекта
    @Column(nullable = false)
    private Instant updatedAt;
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true; // мягкое удаление если юзер неактивный вместо физического deleteuser

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;
}

