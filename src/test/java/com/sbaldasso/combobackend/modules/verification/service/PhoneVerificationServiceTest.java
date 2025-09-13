package com.sbaldasso.combobackend.modules.verification.service;

import com.sbaldasso.combobackend.modules.verification.domain.VerificationCode;
import com.sbaldasso.combobackend.modules.verification.dto.PhoneVerificationRequest;
import com.sbaldasso.combobackend.modules.verification.dto.VerificationResponse;
import com.sbaldasso.combobackend.modules.verification.repository.VerificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PhoneVerificationServiceTest {

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private SMSProvider smsProvider;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private PhoneVerificationService verificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendVerificationCode_shouldGenerateAndSendCode() {
        // Arrange
        PhoneVerificationRequest request = new PhoneVerificationRequest("+5551999999999");
        when(verificationRepository.save(any(VerificationCode.class)))
            .thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(smsProvider).sendSMS(anyString(), anyString());

        // Act
        VerificationResponse response = verificationService.sendVerificationCode(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getRequestId());
        verify(smsProvider).sendSMS(eq(request.getPhoneNumber()), anyString());
        verify(verificationRepository).save(any(VerificationCode.class));
    }

    @Test
    void verifyCode_shouldReturnTrueForValidCode() {
        // Arrange
        String requestId = "test-request-id";
        String code = "123456";
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        verificationCode.setVerified(false);

        when(verificationRepository.findById(requestId))
            .thenReturn(Optional.of(verificationCode));
        when(verificationRepository.save(any(VerificationCode.class)))
            .thenAnswer(i -> i.getArguments()[0]);

        // Act
        boolean result = verificationService.verifyCode(requestId, code);

        // Assert
        assertTrue(result);
        verify(verificationRepository).save(argThat(vc -> vc.isVerified()));
    }

    @Test
    void verifyCode_shouldReturnFalseForInvalidCode() {
        // Arrange
        String requestId = "test-request-id";
        String code = "123456";
        String wrongCode = "654321";
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        verificationCode.setVerified(false);

        when(verificationRepository.findById(requestId))
            .thenReturn(Optional.of(verificationCode));

        // Act
        boolean result = verificationService.verifyCode(requestId, wrongCode);

        // Assert
        assertFalse(result);
        verify(verificationRepository, never()).save(any());
    }

    @Test
    void verifyCode_shouldReturnFalseForExpiredCode() {
        // Arrange
        String requestId = "test-request-id";
        String code = "123456";
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(code);
        verificationCode.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        verificationCode.setVerified(false);

        when(verificationRepository.findById(requestId))
            .thenReturn(Optional.of(verificationCode));

        // Act
        boolean result = verificationService.verifyCode(requestId, code);

        // Assert
        assertFalse(result);
        verify(verificationRepository, never()).save(any());
    }

    @Test
    void sendVerificationCode_shouldThrowExceptionForInvalidPhoneNumber() {
        // Arrange
        PhoneVerificationRequest request = new PhoneVerificationRequest("invalid-number");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            verificationService.sendVerificationCode(request));
        verify(smsProvider, never()).sendSMS(anyString(), anyString());
    }

    @Test
    void sendVerificationCode_shouldRateLimitRequests() {
        // Arrange
        PhoneVerificationRequest request = new PhoneVerificationRequest("+5551999999999");
        when(cacheManager.getCache("verification-attempts")).thenReturn(null);

        // Act & Assert
        verificationService.sendVerificationCode(request); // First attempt
        assertThrows(RuntimeException.class, () -> 
            verificationService.sendVerificationCode(request)); // Second attempt within time window
    }

    @Test
    void verifyCode_shouldHandleNonexistentRequest() {
        // Arrange
        String requestId = "nonexistent-id";
        String code = "123456";

        when(verificationRepository.findById(requestId))
            .thenReturn(Optional.empty());

        // Act
        boolean result = verificationService.verifyCode(requestId, code);

        // Assert
        assertFalse(result);
    }
}
