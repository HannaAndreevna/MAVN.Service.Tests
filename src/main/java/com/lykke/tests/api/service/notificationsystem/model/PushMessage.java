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
@NetClassName("PushNotification")
public class PushMessage {

    private String customerId;
    private String messageTemplateId;
    private Map<String, String> customPayload;
    private Map<String, String> templateParameters;
    private String source;

    public PushMessage() {
        customPayload = new HashMap<>();
        templateParameters = new HashMap<>();
    }
}
