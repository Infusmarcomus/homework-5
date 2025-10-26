package com.example.springdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.springdemo.entity.User;

import java.util.List;
import java.util.Optional;
//интерфейс к БД через JPA что достаем из БД
public interface UserRepository extends JpaRepository<User, Long> {   // JpaRepos - CRUD из коробки
    Optional<User> findByEmail(String email); // кастомный поиск по email/ оptionalUser защита от null/ findByEmail spring сам создаст sql запрос
    boolean existsByEmail(String email);
    //only активные юзеры
    List<User> findAllByIsActiveTrue();

    Optional<User> findById(Long id);
}
