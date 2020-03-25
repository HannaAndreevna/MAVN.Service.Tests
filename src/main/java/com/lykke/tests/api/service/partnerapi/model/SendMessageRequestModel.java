package com.lykke.tests.api.service.partnerapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class SendMessageRequestModel {

    private String partnerId;
    private String customerId;
    private String subject;
    private String message;
    private String locationId;
    private String posId;
}
