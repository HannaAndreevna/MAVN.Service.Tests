package com.lykke.tests.api.service.partnerapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class CustomerBalanceRequestModel {

    private String partnerId;
    private String externalLocationId;
    private String currency;
}
