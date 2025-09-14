package com.sbaldasso.combobackend.modules.notification.service;

import com.sbaldasso.combobackend.modules.notification.dto.SmsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsService {
    private final RestTemplate restTemplate;
    private final String twilioApiKey;
    private final String twilioAccountSid;
    private final String twilioPhoneNumber;

    public SmsService(
            @Value("${twilio.api-key}") String twilioApiKey,
            @Value("${twilio.account-sid}") String twilioAccountSid,
            @Value("${twilio.phone-number}") String twilioPhoneNumber) {
        this.restTemplate = new RestTemplate();
        this.twilioApiKey = twilioApiKey;
        this.twilioAccountSid = twilioAccountSid;
        this.twilioPhoneNumber = twilioPhoneNumber;
    }

    public void sendVerificationCode(String phoneNumber, String code) {
        SmsRequest request = new SmsRequest();
        request.setTo(phoneNumber);
        request.setFrom(twilioPhoneNumber);
        request.setMessage("Seu código de verificação é: " + code);

        // TODO: Implementar chamada real à API do Twilio
        // Por enquanto, apenas logamos o código
        System.out.println("Código de verificação enviado: " + code + " para " + phoneNumber);
    }

    public String generateVerificationCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }
}
