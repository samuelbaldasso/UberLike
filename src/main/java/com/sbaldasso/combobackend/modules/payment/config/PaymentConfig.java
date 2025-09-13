package com.sbaldasso.combobackend.modules.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
@ConfigurationProperties(prefix = "payment")
@Data
public class PaymentConfig {
    private BigDecimal minimumFee = BigDecimal.valueOf(10.0);
    private BigDecimal pricePerKm = BigDecimal.valueOf(2.0);
    private BigDecimal pricePerMinute = BigDecimal.valueOf(0.5);
    private BigDecimal platformFeePercentage = BigDecimal.valueOf(10.0);
    private String mercadoPagoAccessToken;
    private String mercadoPagoPublicKey;
    private boolean testMode = true;
}
