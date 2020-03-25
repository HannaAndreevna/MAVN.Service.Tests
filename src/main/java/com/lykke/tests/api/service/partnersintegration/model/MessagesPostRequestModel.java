package com.lykke.tests.api.service.partnersintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class MessagesPostRequestModel {

    private String partnerId;
    private String customerId;
    private String subject;
    private String message;
    private String externalLocationId;
    private String posId;
    private boolean sendPushNotification;
}
