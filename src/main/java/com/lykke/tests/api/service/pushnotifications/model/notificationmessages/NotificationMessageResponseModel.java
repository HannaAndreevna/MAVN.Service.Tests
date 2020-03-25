package com.lykke.tests.api.service.pushnotifications.model.notificationmessages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationMessageResponseModel {

    private String messageGroupId;
    private Date creationTimestamp;
    private String message;
    private boolean isRead;
    private Map<String, String> customPayload;
}
