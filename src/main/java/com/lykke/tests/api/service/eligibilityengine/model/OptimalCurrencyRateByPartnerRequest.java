package com.lykke.tests.api.service.eligibilityengine.model;

import lombok.Builder;
import lombok.Data;

@Data
public class OptimalCurrencyRateByPartnerRequest extends CurrencyRateRequest {

    private String partnerId;

    @Builder(builderMethodName = "optimalCurrencyRateByPartnerRequestBuilder")
    public OptimalCurrencyRateByPartnerRequest(String fromCurrency, String toCurrency, String customerId,
            String partnerId) {
        super(fromCurrency, toCurrency, customerId);
        this.partnerId = partnerId;
    }
}
