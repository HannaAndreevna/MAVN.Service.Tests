package com.lykke.tests.api.service.partnersintegration.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class BonusCustomerModel {

    private String customerId;
    private String email;
    private float fiatAmount;
    private String currency;
    private String paymentTimestamp;
    private String partnerId;
    private String locationId;
    private String posId;
}
