package com.lykke.tests.api.service.pushnotifications.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CreatePushRegistrationRequestModel {

    private String customerId;
    private String infobipToken;
    private String firebaseToken;
    private String appleToken;
}