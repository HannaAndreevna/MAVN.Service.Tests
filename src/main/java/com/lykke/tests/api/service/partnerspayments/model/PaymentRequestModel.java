package com.lykke.tests.api.service.partnerspayments.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class PaymentRequestModel {

    private String customerId;
    private String partnerId;
    private String locationId;
    private String posId;
    private String tokensAmount;
    private Double fiatAmount;
    private Double totalBillAmount;
    private String currency;
    private String partnerMessageId;
    private int customerExpirationInSeconds;
}
