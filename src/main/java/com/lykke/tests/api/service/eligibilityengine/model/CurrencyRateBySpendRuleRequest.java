package com.lykke.tests.api.service.eligibilityengine.model;

import lombok.Builder;
import lombok.Data;

@Data
public class CurrencyRateBySpendRuleRequest extends CurrencyRateRequest {

    private String spendRuleId;

    @Builder(builderMethodName = "currencyRateBySpendRuleRequestBuilder")
    public CurrencyRateBySpendRuleRequest(String fromCurrency, String toCurrency, String customerId,
            String spendRuleId) {
        super(fromCurrency, toCurrency, customerId);
        this.spendRuleId = spendRuleId;
    }
}
