package com.lykke.tests.api.service.smsprovidermock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class SendSmsRequestModel {

    private String messageId;
    private String phoneNumber;
    private String message;
}
