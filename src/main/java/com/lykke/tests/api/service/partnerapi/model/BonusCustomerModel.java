package com.lykke.tests.api.service.partnerapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class BonusCustomerModel {

    private String customerId;
    private String email;
    private Double fiatAmount;
    private String currency;
    private String paymentTimestamp;
    private String partnerId;
    private String externalLocationId;
    private String posId;
}
