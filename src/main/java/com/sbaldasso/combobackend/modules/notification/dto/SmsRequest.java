package com.sbaldasso.combobackend.modules.notification.dto;

import lombok.Data;

@Data
public class SmsRequest {
    private String to;
    private String from;
    private String message;
}
