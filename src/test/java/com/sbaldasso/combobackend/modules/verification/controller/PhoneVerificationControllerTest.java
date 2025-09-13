package com.sbaldasso.combobackend.modules.verification.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbaldasso.combobackend.modules.verification.dto.PhoneVerificationRequest;
import com.sbaldasso.combobackend.modules.verification.dto.VerificationResponse;
import com.sbaldasso.combobackend.modules.verification.dto.CodeVerificationRequest;
import com.sbaldasso.combobackend.modules.verification.service.PhoneVerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PhoneVerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhoneVerificationService verificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendVerificationCode_shouldReturnRequestId() throws Exception {
        // Arrange
        PhoneVerificationRequest request = new PhoneVerificationRequest("+5551999999999");
        VerificationResponse response = new VerificationResponse("test-request-id");

        when(verificationService.sendVerificationCode(any(PhoneVerificationRequest.class)))
            .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/verification/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value("test-request-id"));
    }

    @Test
    void verifyCode_shouldReturnSuccess() throws Exception {
        // Arrange
        CodeVerificationRequest request = new CodeVerificationRequest("test-request-id", "123456");
        when(verificationService.verifyCode(anyString(), anyString()))
            .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/v1/verification/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));
    }

    @Test
    void sendVerificationCode_withInvalidPhone_shouldReturnBadRequest() throws Exception {
        // Arrange
        PhoneVerificationRequest request = new PhoneVerificationRequest("invalid-number");
        when(verificationService.sendVerificationCode(any()))
            .thenThrow(new IllegalArgumentException("Invalid phone number"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/verification/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyCode_withInvalidRequest_shouldReturnBadRequest() throws Exception {
        // Arrange
        CodeVerificationRequest request = new CodeVerificationRequest("", "");

        // Act & Assert
        mockMvc.perform(post("/api/v1/verification/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void verifyCode_withWrongCode_shouldReturnUnauthorized() throws Exception {
        // Arrange
        CodeVerificationRequest request = new CodeVerificationRequest("test-request-id", "wrong-code");
        when(verificationService.verifyCode(anyString(), anyString()))
            .thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/v1/verification/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sendVerificationCode_withRateLimit_shouldReturnTooManyRequests() throws Exception {
        // Arrange
        PhoneVerificationRequest request = new PhoneVerificationRequest("+5551999999999");
        when(verificationService.sendVerificationCode(any()))
            .thenThrow(new RuntimeException("Rate limit exceeded"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/verification/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests());
    }
}
