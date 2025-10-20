package com.example.springdemo.service;


import com.example.springdemo.entity.User;
import com.example.springdemo.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.Random.class)
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;
    // ---------- createUser ----------
    @Test
    void createUser_shouldSaveUser_whenEmailIsUnique() {
        //given
        User user = new User();
        user.setEmail("artur@mail.com");
        user.setPassword("123456");

        when(userRepository.findByEmail("artur@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hashed123456");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        //when
        User savedUser = userService.createUser(user);

        //then
        assertEquals("hashed123456", savedUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrow_whenEmailExists() {
        //given
        User existingUser = new User();
        existingUser.setEmail("artur@mail.com");

        User newUser = new User();
        newUser.setEmail("artur@mail.com");
        newUser.setPassword("123456");
        //репозиторий находит существующего пользователя
        when(userRepository.findByEmail("artur@mail.com"))
                .thenReturn(Optional.of(existingUser));

        //when+then - выбрасывается исключение
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.createUser(newUser)
        );
        // Проверяем, что сообщение в исключении правильное
        assertEquals("Email уже существует", exception.getReason());
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        // Проверяем, что save() не был вызван
        verify(userRepository, never()).save(any(User.class));

    }
    // ---------- getAllUsers ----------
    @Test
    void getAllUsers_shouldReturnList_whenUsersExist() {
        List<User> mockUsers = List.of(new User(), new User());
        when(userRepository.findAllByIsActiveTrue()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAllByIsActiveTrue();
    }
    @Test
    void getAllUsers_shouldThrow_whenNoUsersFound() {
        when(userRepository.findAllByIsActiveTrue()).thenReturn(List.of());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.getAllUsers()
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Пользователей нет!", ex.getReason()); //надо указать как в классе текст что мы ожидаем
        verify(userRepository).findAllByIsActiveTrue();

    }
    @Test
    void getAllUsers_shouldReturnOnlyActiveUsers() {
        User active = new User();
        active.setId(1L);
        active.setEmail("active@mail.com");
        active.setIsActive(true);

        User inactive = new User();
        inactive.setId(2L);
        inactive.setEmail("inactive@mail.com");
        inactive.setIsActive(false);

        // мок возвращает обоих, но фильтрация на уровне репозитория оставит только активных
        when(userRepository.findAllByIsActiveTrue()).thenReturn(List.of(active));


        List<User> result = userService.getAllUsers();


        assertEquals(1, result.size());
        assertEquals("active@mail.com", result.get(0).getEmail());
        verify(userRepository).findAllByIsActiveTrue();
    }
// ---------- getUserByEmail ----------
@Test
    void getUserByEmail_shouldReturnUser_whenExists() {
        User user = new User();
        user.setEmail("artur@mail.com");
        when(userRepository.findByEmail("artur@mail.com")).
                thenReturn(Optional.of(user));

        User found = userService.getUserByEmail("artur@mail.com");

        assertEquals("artur@mail.com", found.getEmail());
        verify(userRepository).findByEmail("artur@mail.com");
}
@Test
    void getUserByEmail_shouldThrow_whenNotFound() {
        when(userRepository.findByEmail("noemail@mail.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.getUserByEmail("noemail@mail.com")
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Пользователь не найден по email", ex.getReason());
}

    // ---------- getUserById ----------
    @Test
    void getUserById_shouldReturnUser_whenExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User found = userService.getUserById(1L);

        assertEquals(1L, found.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_shouldThrow_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.getUserById(99L)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Пользователь не найден по id", ex.getReason());


    }

// ------------updatedUserById-------------------------------
    @Test
    void updateUser_shouldUpdatedFields_whenUserExists() {
        Long userId = 1L;
        User existingUser = new User();

        existingUser.setId(userId);
        existingUser.setName("OldName");
        existingUser.setLastName("OldLast");
        existingUser.setEmail("old@mail.com");
        existingUser.setAge(25);

        // новые данные, пришедшие из контроллера
        User updates = new User();
        updates.setName("NewName");
        updates.setLastName("NewLast");
        updates.setEmail("new@mail.com");
        updates.setAge(30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updated = userService.updateUser(userId, updates);

        assertEquals("NewName", updated.getName());
        assertEquals("NewLast", updated.getLastName());
        assertEquals("new@mail.com", updated.getEmail());
        assertEquals(30, updated.getAge());
        verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
    }
    @Test
    void updateUser_shouldThrow_whenUserNotFound() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when + then
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.updateUser(99L, new User())
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Пользователь не найден по id", ex.getReason());
        verify(userRepository, never()).save(any());
    }

    // ---------- deleteUserById ----------
    @Test
    void deleteUserById_shouldSetInactive_whenUserExists() {
        User user = new User();
        user.setId(1L);
        user.setIsActive(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

       userService.deleteUserById(1L);

       assertFalse(user.getIsActive());
       verify(userRepository).save(user);

    }
    @Test
    void deleteUserById_shouldThrow_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userService.deleteUserById(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Пользователь не найден по id", ex.getReason());
        verify(userRepository, never()).save(any());
    }
}
