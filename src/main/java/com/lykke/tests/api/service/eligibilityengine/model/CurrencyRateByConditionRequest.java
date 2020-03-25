package com.lykke.tests.api.service.eligibilityengine.model;

import lombok.Builder;
import lombok.Data;

@Data
public class CurrencyRateByConditionRequest extends CurrencyRateRequest {

    private String conditionId;

    @Builder(builderMethodName = "currencyRateByConditionRequestBuilder")
    public CurrencyRateByConditionRequest(String fromCurrency, String toCurrency, String customerId,
            String conditionId) {
        super(fromCurrency, toCurrency, customerId);
        this.conditionId = conditionId;
    }
}
