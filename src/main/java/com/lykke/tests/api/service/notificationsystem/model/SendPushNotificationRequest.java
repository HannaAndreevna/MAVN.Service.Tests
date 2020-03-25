package com.lykke.tests.api.service.notificationsystem.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class SendPushNotificationRequest {

    private String customerId;
    private String messageTemplateId;
    private Map<String, String> customPayload;
    private Map<String, String> templateParameters;
    private String source;
}
