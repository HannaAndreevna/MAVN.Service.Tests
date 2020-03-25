package com.lykke.tests.api.service.notificationsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.tests.api.common.model.ValidationErrorResponseModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationSystemValidationErrorResponseModel extends ValidationErrorResponseModel {

    @JsonProperty("MessageTemplateId")
    private String[] messageTemplateId;
    @JsonProperty("SubjectTemplateId")
    private String[] subjectTemplateId;
    @JsonProperty("TemplateParameters")
    private String[] templateParameters;
    @JsonProperty("CustomerId")
    private String[] customerId;
    @JsonProperty("CustomPayload")
    private String[] customPayload;
}
