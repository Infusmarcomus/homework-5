package com.example.springdemo.service;

import com.example.common.dto.UserEventDto;
import com.example.springdemo.dto.CreateUserDto;
import com.example.springdemo.dto.UpdateUserDto;
import com.example.springdemo.dto.UserResponseDto;
import com.example.springdemo.exceptions.EmailAlreadyExistsException;
import com.example.springdemo.exceptions.UserAlreadyDeletedException;
import com.example.springdemo.exceptions.UserNotFoundException;
import com.example.springdemo.kafka.UserEventProducer;
import com.example.springdemo.mapper.UserMapper;
import com.example.springdemo.model.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.springdemo.entity.User;
import com.example.springdemo.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j // для логирования
@Service
@RequiredArgsConstructor // генит конструктор для final полей
//---------класс для бизнес-логики(проверка преобразование хэширование)
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;
    //добавил к домашнему заданию №5

    private final UserEventProducer userEventProducer;

    private final UserMapper userMapper;


    //---------------метод создания нового пользователя-----------------------

    public UserResponseDto createUser(CreateUserDto createDto) {

        if (userRepository.existsByEmail(createDto.email())) {
            throw new EmailAlreadyExistsException(createDto.email());
        }

        //создание юзера
        User createUser = userMapper.toEntity(createDto);
        createUser.setCreatedAt(Instant.now());

        // хешируем парооль
        String hashedPassword = passwordEncoder.encode(createDto.password());
        createUser.setPassword(hashedPassword);
        log.info("Пароль после хеширования: {}", hashedPassword);


        // присваиваем роль
        createUser.setRole(Role.USER);

        // СОХРАНЯЕМ пользователя в базу
        User savedUser = userRepository.save(createUser);

        log.info("Пользователь сохранен в БД: {}", savedUser.getEmail());

        // Тест Kafka
        log.info("Проверка userEventProducer чето нет логов: {}", userEventProducer != null ? "NOT NULL" : "NULL");
        // вынес метод чуть ниже
        sendUserCreatedEvent(savedUser.getEmail());

        return userMapper.toDto(savedUser);
    }

    // Вынес в отдельный метод для асинхронной отправки
    private void sendUserCreatedEvent(String email) {
        try {
            UserEventDto event = UserEventDto.created(email);
            userEventProducer.sendUserEvent(event);
            log.info("Событие USER_CREATED отправлено в Kafka");
        } catch (Exception e) {
            log.error("Ошибка при отправке в Kafka: {}", e.getMessage());
        }
    }


    //------------метод поиска всех пользователей-----------------

    public List<UserResponseDto> getAllUsers() {
        List<User> userEntities = userRepository.findAllByIsActiveTrue();

        if (userEntities.isEmpty()) {
            throw new UserNotFoundException("Пользователи не найдены");
        }

        return userEntities.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    //------------найти пользователя по email-------------

    public UserResponseDto getUserByEmail(String email) {
        log.info("Ищем пользователя по email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return userMapper.toDto(user);
    }

    // -----------найти по id------------
    public UserResponseDto getUserById(Long id) {
        log.info("Ищем пользователя по id: {}", id);
        User userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDto(userEntity);

    }

    // --------обновить пользователя-----------
    // В UserService добавь этот метод:
    public UserResponseDto updateUser(Long id, UpdateUserDto updateDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Обновляем только те поля, которые пришли в DTO (не null)
        if (updateDto.name() != null) {
            existingUser.setName(updateDto.name());
        }
        if (updateDto.lastName() != null) {
            existingUser.setLastName(updateDto.lastName());
        }
        if (updateDto.email() != null) {
            // Проверяем уникальность email, если он меняется
            if (!updateDto.email().equals(existingUser.getEmail()) &&
                    userRepository.existsByEmail(updateDto.email())) {
                throw new EmailAlreadyExistsException(updateDto.email());
            }
            existingUser.setEmail(updateDto.email());
        }
        if (updateDto.age() != null) {
            existingUser.setAge(updateDto.age());
        }

        existingUser.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDto(updatedUser);
    }

    // -----------удалить по id------------
    public void deleteUserById(Long id) {
        log.info("Попытка удалить пользователя с ID: {}", id);

        // есть ли пользователь в базе
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", id);
                    return new UserNotFoundException(id);
                });

        // не удалён ли он уже
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            log.warn("Пользователь с ID {} уже неактивен, повторное удаление не требуется", id);
            throw new UserAlreadyDeletedException(id);
        }
        String userEmail = user.getEmail();
        // мягкое удаление
        user.setIsActive(false);

        userRepository.save(user);
        // по аналогии выношу ниже
        sendUserDeletedEvent(user.getEmail());

        log.info("Пользователь с ID {} успешно помечен как неактивный", id);
    }

    private void sendUserDeletedEvent(String email) {
        try {
            UserEventDto event = UserEventDto.deleted(email);
            userEventProducer.sendUserEvent(event);
            log.info("Событие USER_DELETED отправлено в Kafka");
        } catch (Exception e) {
            log.error("Ошибка при отправке в Kafka: {}", e.getMessage());
        }


    }
}



