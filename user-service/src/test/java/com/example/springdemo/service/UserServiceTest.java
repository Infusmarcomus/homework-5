package com.example.springdemo.service;

import com.example.common.dto.UserEventDto;
import com.example.springdemo.dto.CreateUserDto;
import com.example.springdemo.dto.UpdateUserDto;
import com.example.springdemo.dto.UserResponseDto;
import com.example.springdemo.entity.User;
import com.example.springdemo.exceptions.EmailAlreadyExistsException;
import com.example.springdemo.exceptions.UserNotFoundException;
import com.example.springdemo.exceptions.UserAlreadyDeletedException;
import com.example.springdemo.kafka.UserEventProducer;
import com.example.springdemo.mapper.UserMapper;
import com.example.springdemo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserEventProducer userEventProducer;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    // Вспомогательный метод для создания UserResponseDto
    private UserResponseDto createUserResponseDto(Long id, String name, String lastName, String email, Integer age) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(id);
        dto.setName(name);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setAge(age);
        return dto;
    }

    // Тест 1: Успешное создание пользователя
    @Test
    void createUser_WhenEmailNotExists_ShouldCreateUser() {
        // Given
        CreateUserDto createDto = new CreateUserDto("Артур", "Марченко", "artur@mail.ru", 25, "123456");
        User userEntity = new User();
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("artur@mail.ru");
        UserResponseDto responseDto = createUserResponseDto(1L, "Артур", "Марченко", "artur@mail.ru", 25);

        when(userRepository.existsByEmail("artur@mail.ru")).thenReturn(false);
        when(userMapper.toEntity(createDto)).thenReturn(userEntity);
        when(passwordEncoder.encode("123456")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(responseDto);

        // Mock Kafka
        CompletableFuture<Void> kafkaFuture = CompletableFuture.completedFuture(null);
        when(userEventProducer.sendUserEvent(any(UserEventDto.class))).thenReturn(kafkaFuture);

        // When
        UserResponseDto result = userService.createUser(createDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Артур", result.getName());
        assertEquals("Марченко", result.getLastName());
        assertEquals("artur@mail.ru", result.getEmail());
        assertEquals(25, result.getAge());

        verify(userRepository).existsByEmail("artur@mail.ru");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("123456");
        verify(userEventProducer).sendUserEvent(any(UserEventDto.class));
    }

    // Тест 2: Создание пользователя с существующим email
    @Test
    void createUser_WhenEmailExists_ShouldThrowEmailAlreadyExistsException() {
        // Given
        CreateUserDto createDto = new CreateUserDto("Артур", "Марченко", "existing@mail.ru", 25, "123456");
        when(userRepository.existsByEmail("existing@mail.ru")).thenReturn(true);

        // When & Then
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.createUser(createDto);
        });

        assertTrue(exception.getMessage().contains("existing@mail.ru"));
        verify(userRepository, never()).save(any(User.class));
        verify(userEventProducer, never()).sendUserEvent(any(UserEventDto.class));
    }

    // Тест 3: Успешное обновление пользователя
    @Test
    void updateUser_WhenUserExists_ShouldUpdateUser() {
        // Given
        Long userId = 1L;
        UpdateUserDto updateDto = new UpdateUserDto("Артур", "Дмитриев", "artur.dmitriev@mail.ru", 30);

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Артур");
        existingUser.setLastName("Марченко");
        existingUser.setEmail("artur@mail.ru");
        existingUser.setAge(25);

        User updatedUser = new User();
        updatedUser.setId(userId);
        UserResponseDto responseDto = createUserResponseDto(1L, "Артур", "Дмитриев", "artur.dmitriev@mail.ru", 30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("artur.dmitriev@mail.ru")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(responseDto);

        // When
        UserResponseDto result = userService.updateUser(userId, updateDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Артур", result.getName());
        assertEquals("Дмитриев", result.getLastName());
        assertEquals("artur.dmitriev@mail.ru", result.getEmail());
        assertEquals(30, result.getAge());

        verify(userRepository).save(existingUser);
        assertNotNull(existingUser.getUpdatedAt());
    }

    // Тест 4: Обновление пользователя с существующим email
    @Test
    void updateUser_WhenEmailExists_ShouldThrowEmailAlreadyExistsException() {
        // Given
        Long userId = 1L;
        UpdateUserDto updateDto = new UpdateUserDto("Артур", "Марченко", "existing@mail.ru", 30);

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("artur@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("existing@mail.ru")).thenReturn(true);

        // When & Then
        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            userService.updateUser(userId, updateDto);
        });

        assertTrue(exception.getMessage().contains("existing@mail.ru"));
        verify(userRepository, never()).save(any(User.class));
    }

    // Тест 5: Получение пользователя по несуществующему ID
    @Test
    void getUserById_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        assertTrue(exception.getMessage().contains("999"));
    }

    // Тест 6: Успешное получение пользователя по ID
    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("artur@mail.ru");
        UserResponseDto responseDto = createUserResponseDto(1L, "Артур", "Марченко", "artur@mail.ru", 25);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        // When
        UserResponseDto result = userService.getUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("artur@mail.ru", result.getEmail());
        verify(userRepository).findById(userId);
    }

    // Тест 7: Успешное получение пользователя по email
    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        String email = "artur@mail.ru";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        UserResponseDto responseDto = createUserResponseDto(1L, "Артур", "Марченко", "artur@mail.ru", 25);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        // When
        UserResponseDto result = userService.getUserByEmail(email);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("artur@mail.ru", result.getEmail());
        verify(userRepository).findByEmail(email);
    }

    // Тест 8: Получение пользователя по несуществующему email
    @Test
    void getUserByEmail_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Given
        String email = "nonexistent@mail.ru";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByEmail(email);
        });

        assertTrue(exception.getMessage().contains(email));
    }

    // Тест 9: Удаление пользователя
    @Test
    void deleteUserById_WhenUserExistsAndActive_ShouldSoftDelete() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setIsActive(true);
        user.setEmail("artur@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Mock Kafka
        CompletableFuture<Void> kafkaFuture = CompletableFuture.completedFuture(null);
        when(userEventProducer.sendUserEvent(any(UserEventDto.class))).thenReturn(kafkaFuture);

        // When
        userService.deleteUserById(userId);

        // Then
        verify(userRepository).save(user);
        assertFalse(user.getIsActive());
        verify(userEventProducer).sendUserEvent(any(UserEventDto.class));
    }

    // Тест 10: Удаление уже удаленного пользователя
    @Test
    void deleteUserById_WhenUserAlreadyInactive_ShouldThrowUserAlreadyDeletedException() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setIsActive(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When & Then
        UserAlreadyDeletedException exception = assertThrows(UserAlreadyDeletedException.class, () -> {
            userService.deleteUserById(userId);
        });

        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(userRepository, never()).save(any(User.class));
    }

    // Тест 11: Удаление несуществующего пользователя
    @Test
    void deleteUserById_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUserById(userId);
        });

        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(userRepository, never()).save(any(User.class));
    }

    // Тест 12: Получение всех активных пользователей
    @Test
    void getAllUsers_WhenActiveUsersExist_ShouldReturnUsers() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        user1.setIsActive(true);

        User user2 = new User();
        user2.setId(2L);
        user2.setIsActive(true);

        UserResponseDto dto1 = createUserResponseDto(1L, "Артур", "Марченко", "artur@mail.ru", 25);
        UserResponseDto dto2 = createUserResponseDto(2L, "Мария", "Иванова", "maria@mail.ru", 30);

        when(userRepository.findAllByIsActiveTrue()).thenReturn(List.of(user1, user2));
        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);

        // When
        List<UserResponseDto> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("artur@mail.ru", result.get(0).getEmail());
        assertEquals(2L, result.get(1).getId());
        assertEquals("maria@mail.ru", result.get(1).getEmail());
        verify(userRepository).findAllByIsActiveTrue();
    }

    // Тест 13: Получение всех пользователей когда нет активных
    @Test
    void getAllUsers_WhenNoActiveUsers_ShouldThrowUserNotFoundException() {
        // Given
        when(userRepository.findAllByIsActiveTrue()).thenReturn(List.of());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getAllUsers();
        });

        assertTrue(exception.getMessage().contains("не найден"));
    }
}