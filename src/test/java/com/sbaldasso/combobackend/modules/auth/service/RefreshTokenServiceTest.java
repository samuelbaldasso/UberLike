package com.sbaldasso.combobackend.modules.auth.service;

import com.sbaldasso.combobackend.modules.auth.domain.RefreshToken;
import com.sbaldasso.combobackend.modules.auth.repository.RefreshTokenRepository;
import com.sbaldasso.combobackend.modules.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User testUser;
    private RefreshToken testRefreshToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", 604800L);

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("test@example.com");

        testRefreshToken = new RefreshToken();
        testRefreshToken.setId(UUID.randomUUID());
        testRefreshToken.setUser(testUser);
        testRefreshToken.setToken("valid-token");
        testRefreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));
        testRefreshToken.setRevoked(false);
    }

    @Test
    void createRefreshToken_ShouldCreateNewToken() {
        // Arrange
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getUser());
        assertFalse(result.isRevoked());
        verify(refreshTokenRepository).revokeAllUserTokens(testUser.getId());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void verifyAndRefresh_WithValidToken_ShouldCreateNewToken() {
        // Arrange
        when(refreshTokenRepository.findByToken(testRefreshToken.getToken()))
                .thenReturn(Optional.of(testRefreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testRefreshToken);

        // Act
        RefreshToken result = refreshTokenService.verifyAndRefresh(testRefreshToken.getToken());

        // Assert
        assertNotNull(result);
        assertTrue(testRefreshToken.isRevoked());
        verify(refreshTokenRepository).save(testRefreshToken);
    }

    @Test
    void verifyAndRefresh_WithInvalidToken_ShouldThrowException() {
        // Arrange
        when(refreshTokenRepository.findByToken("invalid-token"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> refreshTokenService.verifyAndRefresh("invalid-token"));
    }

    @Test
    void verifyAndRefresh_WithExpiredToken_ShouldThrowException() {
        // Arrange
        testRefreshToken.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(refreshTokenRepository.findByToken(testRefreshToken.getToken()))
                .thenReturn(Optional.of(testRefreshToken));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> refreshTokenService.verifyAndRefresh(testRefreshToken.getToken()));
    }

    @Test
    void verifyAndRefresh_WithRevokedToken_ShouldThrowException() {
        // Arrange
        testRefreshToken.setRevoked(true);
        when(refreshTokenRepository.findByToken(testRefreshToken.getToken()))
                .thenReturn(Optional.of(testRefreshToken));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> refreshTokenService.verifyAndRefresh(testRefreshToken.getToken()));
    }

    @Test
    void revokeRefreshToken_ShouldRevokeToken() {
        // Arrange
        when(refreshTokenRepository.findByToken(testRefreshToken.getToken()))
                .thenReturn(Optional.of(testRefreshToken));

        // Act
        refreshTokenService.revokeRefreshToken(testRefreshToken.getToken());

        // Assert
        assertTrue(testRefreshToken.isRevoked());
        verify(refreshTokenRepository).save(testRefreshToken);
    }

    @Test
    void cleanupExpiredTokens_ShouldDeleteExpiredTokens() {
        // Act
        refreshTokenService.cleanupExpiredTokens();

        // Assert
        verify(refreshTokenRepository).deleteExpiredTokens(any(LocalDateTime.class));
    }
}
