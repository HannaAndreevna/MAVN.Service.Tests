package com.lykke.tests.api.service.notificationsystemaudit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryFailedAuditMessageResponseModel {

    private String creationTimestamp;
    private String sentTimestamp;
    private String messageId;
    private String messageType;
    private String customerId;
    private String subjectTemplateId;
    private String messageTemplateId;
    private String source;
    private String callType;
    private String deliveryComment;
}
