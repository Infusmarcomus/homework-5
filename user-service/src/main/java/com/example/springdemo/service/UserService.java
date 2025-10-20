package com.example.springdemo.service;

import com.example.common.dto.UserEventDto;
import com.example.springdemo.kafka.UserEventProducer;
import com.example.springdemo.model.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.springdemo.entity.User;
import com.example.springdemo.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Slf4j // для логирования
@Service
@RequiredArgsConstructor // генит конструктор для final полей
//---------класс для бизнес-логики(проверка преобразование хэширование)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    //добавил к домашнему заданию №5
    private final UserEventProducer userEventProducer;

    //---------------метод создания нового пользователя-----------------------

    public User createUser(User user) {

        //проверка уникальности email
        userRepository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже существует");
        });

        log.info("Пароль до хеширования: {}", user.getPassword());

        //хэшируем пароль перед сохранением
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        //сохраняем в базу
        log.info("Пароль после хеширования: {}", hashedPassword);

        user.setRole(Role.USER);
        // СОХРАНЯЕМ пользователя в базу
        User savedUser = userRepository.save(user);
        log.info("Пользователь сохранен в БД: {}", savedUser.getEmail());

        // Тест Kafka
        log.info("Проверка userEventProducer чето нет логов: {}", userEventProducer != null ? "NOT NULL" : "NULL");

        try {
            UserEventDto event = UserEventDto.created(savedUser.getEmail());
            userEventProducer.sendUserEvent(event)
                    .get(5, TimeUnit.SECONDS); // ← Ждем завершения
            log.info("Событие подтверждено Kafka");
        } catch (Exception e) {
            log.error("Ошибка Kafka: {}", e.getMessage());
        }

        return savedUser;
    }

    //------------метод поиска всех пользователей-----------------

    public List<User> getAllUsers() {
         List<User> users = userRepository.findAllByIsActiveTrue();

        if(users.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователей нет!");
        }
            return users;
    }

    //------------найти пользователя по email-------------

    public User getUserByEmail(String email) {
        log.info("Ищем пользователя по email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден по email"));

    }

    // -----------найти по id------------
    public User getUserById(Long id) {
        log.info("Ищем пользователя по id: {}", id);
         return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден по id"));
    }

    // --------обновить пользователя-----------
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден по id"));

        existingUser.setName(userDetails.getName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setAge(userDetails.getAge());

        return userRepository.save(existingUser);

    }
    // -----------удалить по id------------
    public void deleteUserById(Long id) {
        log.info("Попытка удалить пользователя с ID: {}", id);

        // есть ли пользователь в базе
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пользователь с ID {} не найден", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден по id");
                });

        // не удалён ли он уже
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            log.warn("Пользователь с ID {} уже неактивен, повторное удаление не требуется", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователь уже удалён");
        }
        String userEmail = user.getEmail();
        // мягкое удаление
        user.setIsActive(false);

        userRepository.save(user);

        try {
            UserEventDto event = UserEventDto.deleted(userEmail);
            userEventProducer.sendUserEvent(event);
            log.info("Событие USER_DELETED отправлено в Kafka для пользователя: {}", userEmail);

        } catch (Exception e) {
            log.error("Ошибка при отправке события в Kafka: {}", e.getMessage());
        }

        log.info("Пользователь с ID {} успешно помечен как неактивный", id);
    }
}



