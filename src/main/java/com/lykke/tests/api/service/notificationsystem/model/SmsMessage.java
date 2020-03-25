package com.lykke.tests.api.service.notificationsystem.model;

import com.lykke.api.testing.annotations.NetClassName;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@NetClassName("Sms")
public class SmsMessage {

    private String customerId;
    private String messageTemplateId;
    private Map<String, String> templateParameters;
    private String source;

    public SmsMessage() {
        templateParameters = new HashMap<>();
    }
}
