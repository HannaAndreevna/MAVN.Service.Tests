package com.lykke.tests.api.service.eligibilityengine.model;

import lombok.Builder;
import lombok.Data;

@Data
public class CurrencyRateByEarnRuleRequest extends CurrencyRateRequest {

    private String earnRuleId;

    @Builder(builderMethodName = "currencyRateByEarnRuleRequestBuilder")
    public CurrencyRateByEarnRuleRequest(String fromCurrency, String toCurrency, String customerId,
            String earnRuleId) {
        super(fromCurrency, toCurrency, customerId);
        this.earnRuleId = earnRuleId;
    }
}
