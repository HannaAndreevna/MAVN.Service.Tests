package com.lykke.tests.api.service.notificationsystemaudit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditMessageResponseModel {

    @EqualsAndHashCode.Exclude
    private String id;
    @EqualsAndHashCode.Exclude
    private Date creationTimestamp;
    @EqualsAndHashCode.Exclude
    private Date sentTimestamp;
    @EqualsAndHashCode.Exclude
    private String messageId;
    private String messageType;
    private String customerId;
    @EqualsAndHashCode.Exclude
    private String subjectTemplateId;
    private String messageTemplateId;
    private String source;
    private String callType;
    private String formattingStatus;
    private String formattingComment;
    private String deliveryStatus;
    private String deliveryComment;
}
