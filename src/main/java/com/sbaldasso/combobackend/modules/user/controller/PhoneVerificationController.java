package com.sbaldasso.combobackend.modules.user.controller;

import com.sbaldasso.combobackend.modules.user.service.PhoneVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users/phone-verification")
@RequiredArgsConstructor
public class PhoneVerificationController {

    private final PhoneVerificationService phoneVerificationService;

    @PostMapping("/send-code")
    @PreAuthorize("@securityService.isCurrentUser(#userId)")
    public ResponseEntity<Void> sendVerificationCode(@RequestAttribute UUID userId) {
        phoneVerificationService.sendVerificationCode(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    @PreAuthorize("@securityService.isCurrentUser(#userId)")
    public ResponseEntity<Boolean> verifyCode(
            @RequestAttribute UUID userId,
            @RequestParam String code) {
        boolean verified = phoneVerificationService.verifyCode(userId, code);
        return ResponseEntity.ok(verified);
    }
}
