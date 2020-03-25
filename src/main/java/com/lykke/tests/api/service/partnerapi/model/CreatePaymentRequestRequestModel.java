package com.lykke.tests.api.service.partnerapi.model;

import com.lykke.api.testing.annotations.PublicApi;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@PublicApi
public class CreatePaymentRequestRequestModel {

    private String customerId;
    private String totalFiatAmount;
    private String fiatAmount;
    private String currency;
    private String tokensAmount;
    private String paymentInfo;
    private String partnerId;
    private String locationId;
    private String posId;
    private String paymentProcessedCallbackUrl;
    private int expirationTimeoutInSeconds;
}
