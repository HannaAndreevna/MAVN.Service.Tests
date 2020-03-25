package com.lykke.tests.api.service.partnersintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PaymentsCreateRequestModel {

    private String customerId;
    private float totalFiatAmount;
    private float fiatAmount;
    private String currency;
    private String tokensAmount;
    private String paymentInfo;
    private String partnerId;
    private String externalLocationId;
    private String posId;
    private String paymentProcessedCallbackUrl;
    private String requestAuthToken;
    private int expirationTimeoutInSeconds;
}

