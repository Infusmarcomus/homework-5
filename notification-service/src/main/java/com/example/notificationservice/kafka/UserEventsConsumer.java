package com.example.notificationservice.kafka;

import com.example.common.dto.UserEventDto;
import com.example.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventsConsumer {

    private final EmailService emailService;

    @KafkaListener(topics = "user-registration-topic", groupId = "notification-group")
    public void consumeUserEvent(UserEventDto event) {
        try {
            log.info("Получено событие из Kafka: {}", event);

            if ("USER_CREATED".equals(event.eventType())) {
                emailService.sendWelcomeEmail(event.email());
            } else if ("USER_DELETED".equals(event.eventType())) {
                emailService.sendGoodbyeEmail(event.email());
            } else {
                log.warn("Неизвестный тип события: {}", event.eventType());
            }

        } catch (Exception e) {
            log.error("Ошибка обработки события: {}", event, e);

        }
    }
}