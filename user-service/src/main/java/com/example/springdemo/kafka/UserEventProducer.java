package com.example.springdemo.kafka;


import com.example.common.dto.UserEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

//------------------kafka producer-----------------
@Component
@Slf4j
public class UserEventProducer {
    private static final String USER_EVENTS_TOPIC = "user-registration-topic";

    @Autowired
    private KafkaTemplate<String, UserEventDto> kafkaTemplate;

    public CompletableFuture<Void> sendUserEvent(UserEventDto event) {
        log.info("Отправка события в Kafka: {}", event);

        return kafkaTemplate.send(USER_EVENTS_TOPIC, event.email(), event)  // ← event.email() теперь работает!
                .thenAccept(result -> {
                    log.info("Событие успешно отправлено в топик: {}, partition: {}, offset: {}",
                            USER_EVENTS_TOPIC,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                })
                .exceptionally(ex -> {
                    log.error("Ошибка отправки события в Kafka: {}", ex.getMessage());
                    return null;
                });
    }
}