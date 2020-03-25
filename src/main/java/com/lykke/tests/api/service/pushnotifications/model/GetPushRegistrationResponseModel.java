package com.lykke.tests.api.service.pushnotifications.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetPushRegistrationResponseModel {

    private UUID id;
    private Date registrationDate;
    private String customerId;
    private String infobipToken;
    private String firebaseToken;
    private String appleToken;
}
