package com.example.notificationservice.controller;

import com.example.notificationservice.dto.EmailRequest;
import com.example.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        try {
            emailService.sendCustomEmail(request.getTo(), request.getSubject(), request.getMessage());
            return ResponseEntity.ok("Письмо успешно отправлено");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/welcome")
    public ResponseEntity<String> sendWelcomeEmail(@RequestParam String email) {
        try {
            emailService.sendWelcomeEmail(email);
            return ResponseEntity.ok("Приветственное письмо отправлено");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка в отправлении приветственного письма: " + e.getMessage());
        }
    }

    @PostMapping("/goodbye")
    public ResponseEntity<String> sendGoodbyeEmail(@RequestParam String email) {
        try {
            emailService.sendGoodbyeEmail(email);
            return ResponseEntity.ok("Прощальное письмо отправлено");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка отправки прощального письма: " + e.getMessage());
        }
    }
}