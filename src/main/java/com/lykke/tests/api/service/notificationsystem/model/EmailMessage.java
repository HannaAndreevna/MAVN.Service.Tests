package com.lykke.tests.api.service.notificationsystem.model;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class EmailMessage {
    private String customerId;
    private String subjectTemplateId;
    private String messageTemplateId;
    private Map<String, String> templateParameters;
    private String source;

    public EmailMessage() {
        templateParameters = new HashMap<>();
    }
}
