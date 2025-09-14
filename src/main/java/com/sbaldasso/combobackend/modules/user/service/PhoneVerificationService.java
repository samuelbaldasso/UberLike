package com.sbaldasso.combobackend.modules.user.service;

import com.sbaldasso.combobackend.modules.notification.service.SmsService;
import com.sbaldasso.combobackend.modules.user.domain.User;
import com.sbaldasso.combobackend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhoneVerificationService {
    private final SmsService smsService;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    private static final String VERIFICATION_CODE_PREFIX = "phone_verification:";
    private static final Duration CODE_EXPIRATION = Duration.ofMinutes(5);

    public void sendVerificationCode(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String verificationCode = smsService.generateVerificationCode();
        String redisKey = VERIFICATION_CODE_PREFIX + userId;

        // Armazena o código no Redis com expiração
        redisTemplate.opsForValue().set(redisKey, verificationCode, CODE_EXPIRATION);

        // Envia o SMS
        smsService.sendVerificationCode(user.getPhone(), verificationCode);
    }

    public boolean verifyCode(UUID userId, String code) {
        String redisKey = VERIFICATION_CODE_PREFIX + userId;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode != null && storedCode.equals(code)) {
            // Remove o código após verificação bem-sucedida
            redisTemplate.delete(redisKey);

            // Atualiza o status de verificação do usuário
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
            user.setPhoneVerified(true);
            userRepository.save(user);

            return true;
        }

        return false;
    }
}
