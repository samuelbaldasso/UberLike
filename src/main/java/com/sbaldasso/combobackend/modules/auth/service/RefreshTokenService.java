package com.sbaldasso.combobackend.modules.auth.service;

import com.sbaldasso.combobackend.modules.auth.domain.RefreshToken;
import com.sbaldasso.combobackend.modules.auth.repository.RefreshTokenRepository;
import com.sbaldasso.combobackend.modules.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    @Value("${security.refresh-token.expiration:604800}")  // 7 dias em segundos
    private long refreshTokenExpiration;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Revoga todos os tokens ativos do usuário
        refreshTokenRepository.revokeAllUserTokens(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(generateToken());
        refreshToken.setExpiryDate(LocalDateTime.now().plusSeconds(refreshTokenExpiration));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken verifyAndRefresh(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Refresh token não encontrado"));

        if (!refreshToken.isValid()) {
            throw new IllegalStateException("Refresh token expirado ou revogado");
        }

        // Revoga o token atual
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // Cria um novo token
        return createRefreshToken(refreshToken.getUser());
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Refresh token não encontrado"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    private String generateToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return encoder.encodeToString(tokenBytes);
    }

    @Scheduled(cron = "0 0 * * * *") // Executa a cada hora
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
