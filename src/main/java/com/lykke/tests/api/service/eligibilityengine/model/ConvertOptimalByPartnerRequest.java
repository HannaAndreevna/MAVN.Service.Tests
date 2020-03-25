package com.lykke.tests.api.service.eligibilityengine.model;

import lombok.Builder;
import lombok.Data;

@Data
public class ConvertOptimalByPartnerRequest extends ConvertAmountRequest {

    private String partnerId;

    @Builder(builderMethodName = "convertOptimalByPartnerRequestBuilder")
    public ConvertOptimalByPartnerRequest(String fromCurrency, String toCurrency, String customerId, String amount,
            String partnerId) {
        super(fromCurrency, toCurrency, customerId, amount);
        this.partnerId = partnerId;
    }
}
