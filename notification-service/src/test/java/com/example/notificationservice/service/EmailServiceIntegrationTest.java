package com.example.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Test
    void sendWelcomeEmail_ShouldNotThrowException() {
        // Given
        String testEmail = "test.welcome@example.com";

        // When & Then
        assertDoesNotThrow(() -> {
            emailService.sendWelcomeEmail(testEmail);
        });

        log.info("Поздравляем с успешной регистрацией: {}", testEmail);
    }

    @Test
    void sendGoodbyeEmail_ShouldNotThrowException() {
        // Given
        String testEmail = "test.goodbye@example.com";

        // When & Then
        assertDoesNotThrow(() -> {
            emailService.sendGoodbyeEmail(testEmail);
        });

        log.info("Очень жаль расставаться, ждем вашего возвращения: {}", testEmail);
    }

    @Test
    void sendCustomEmail_ShouldNotThrowException() {
        // Given
        String testEmail = "test.custom@example.com";
        String subject = "Test Subject";
        String message = "Test Message";

        // When & Then
        assertDoesNotThrow(() -> {
            emailService.sendCustomEmail(testEmail, subject, message);
        });

        log.info("Кастомная почта отправлена: {}", testEmail);
    }
}