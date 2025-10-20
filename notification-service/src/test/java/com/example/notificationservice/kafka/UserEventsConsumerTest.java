package com.example.notificationservice.kafka;

import com.example.common.dto.UserEventDto;
import com.example.notificationservice.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserEventsConsumerTest {

    @Autowired
    private UserEventsConsumer userEventsConsumer;

    @SpyBean
    private EmailService emailService;

    @Test
    void consumeUserEvent_WithUserCreatedEvent_ShouldSendWelcomeEmail() {
        // Given
        UserEventDto userCreatedEvent = new UserEventDto(
                "USER_CREATED",
                "test.kafka@example.com",
                Instant.now()
        );

        // When
        userEventsConsumer.consumeUserEvent(userCreatedEvent);

        // Then
        verify(emailService, times(1)).sendWelcomeEmail("test.kafka@example.com");
        verify(emailService, never()).sendGoodbyeEmail(anyString());
    }

    @Test
    void consumeUserEvent_WithUserDeletedEvent_ShouldSendGoodbyeEmail() {
        // Given
        UserEventDto userDeletedEvent = new UserEventDto(
                "USER_DELETED",
                "test.kafka@example.com",
                Instant.now()
        );

        // When
        userEventsConsumer.consumeUserEvent(userDeletedEvent);

        // Then
        verify(emailService, times(1)).sendGoodbyeEmail("test.kafka@example.com");
        verify(emailService, never()).sendWelcomeEmail(anyString());
    }

    @Test
    void consumeUserEvent_WithUnknownEventType_ShouldNotSendAnyEmail() {
        // Given
        UserEventDto unknownEvent = new UserEventDto(
                "UNKNOWN_EVENT",
                "test.kafka@example.com",
                Instant.now()
        );

        // When
        userEventsConsumer.consumeUserEvent(unknownEvent);

        // Then
        verify(emailService, never()).sendWelcomeEmail(anyString());
        verify(emailService, never()).sendGoodbyeEmail(anyString());
    }
}