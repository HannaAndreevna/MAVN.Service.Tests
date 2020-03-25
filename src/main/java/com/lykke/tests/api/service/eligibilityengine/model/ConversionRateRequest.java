package com.lykke.tests.api.service.eligibilityengine.model;

import com.lykke.api.testing.annotations.QueryParameters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@QueryParameters
public class ConversionRateRequest {

    private String partnerId;
    private String customerId;
}
