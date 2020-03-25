package com.lykke.tests.api.service.partnersintegration.model;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@QueryParameters
public class CustomerBalanceRequestModel {

    private String partnerId;
    private String locationId;
    private String currency;
}
