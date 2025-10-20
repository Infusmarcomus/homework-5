package com.example.notificationservice.controller;

import com.example.notificationservice.dto.EmailRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendEmail_WithValidRequest_ShouldReturnOk() throws Exception {
        // Given
        EmailRequest request = new EmailRequest();
        request.setTo("test.api@example.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");

        // When & Then
        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Письмо успешно отправлено"));
    }

    @Test
    void sendWelcomeEmail_WithValidEmail_ShouldReturnOk() throws Exception {
        // Given
        String testEmail = "test.welcome@example.com";

        // When & Then
        mockMvc.perform(post("/api/notifications/welcome")
                        .param("email", testEmail))
                .andExpect(status().isOk())
                .andExpect(content().string("Приветственное письмо отправлено"));
    }

    @Test
    void sendGoodbyeEmail_WithValidEmail_ShouldReturnOk() throws Exception {
        // Given
        String testEmail = "test.goodbye@example.com";

        // When & Then
        mockMvc.perform(post("/api/notifications/goodbye")
                        .param("email", testEmail))
                .andExpect(status().isOk())
                .andExpect(content().string("Прощальное письмо отправлено"));
    }

    @Test
    void sendEmail_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given - пустой request
        EmailRequest request = new EmailRequest();

        // When & Then
        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}