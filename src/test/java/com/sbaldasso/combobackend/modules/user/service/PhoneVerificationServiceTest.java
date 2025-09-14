package com.sbaldasso.combobackend.modules.user.service;

import com.sbaldasso.combobackend.modules.notification.service.SmsService;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PhoneVerificationServiceTest {

    @Mock
    private SmsService smsService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private PhoneVerificationService phoneVerificationService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setPhone("+5551999999999");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void shouldSendVerificationCode() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(smsService.generateVerificationCode()).thenReturn("123456");

        // Act
        phoneVerificationService.sendVerificationCode(userId);

        // Assert
        verify(valueOperations).set(anyString(), eq("123456"), any(Duration.class));
        verify(smsService).sendVerificationCode(testUser.getPhone(), "123456");
    }

    @Test
    void shouldVerifyCodeSuccessfully() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(valueOperations.get(anyString())).thenReturn("123456");

        // Act
        boolean result = phoneVerificationService.verifyCode(userId, "123456");

        // Assert
        assertTrue(result);
        verify(redisTemplate).delete(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldFailVerificationWithWrongCode() {
        // Arrange
        when(valueOperations.get(anyString())).thenReturn("123456");

        // Act
        boolean result = phoneVerificationService.verifyCode(userId, "654321");

        // Assert
        assertFalse(result);
        verify(redisTemplate, never()).delete(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> phoneVerificationService.sendVerificationCode(userId));
    }
}
