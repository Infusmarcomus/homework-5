package com.example.notificationservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    public void sendWelcomeEmail(String userEmail) {
        String subject = "Добро пожаловать!";
        String message = "Здравствуйте! Ваш аккаунт на сайте был успешно создан.";
        sendEmail(userEmail, subject, message);
    }

    public void sendGoodbyeEmail(String userEmail) {
        String subject = "Аккаунт удален";
        String message = "Здравствуйте! Ваш аккаунт был удалён.";
        sendEmail(userEmail, subject, message);
    }

    public void sendCustomEmail(String userEmail, String subject, String message) {
        sendEmail(userEmail, subject, message);
    }

    private void sendEmail(String to, String subject, String message) {
        // JavaMailSender или SendGrid/Mailgun или другой сервис

        log.info("Письмо отправлено:");
        log.info("  Кому: {}", to);
        log.info("  Обьект: {}", subject);
        log.info("  Сообщение: {}", message);
        log.info("  Статус: Успешно отправлено");
    }
}