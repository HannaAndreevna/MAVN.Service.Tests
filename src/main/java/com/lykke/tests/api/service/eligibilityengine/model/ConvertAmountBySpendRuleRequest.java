package com.lykke.tests.api.service.eligibilityengine.model;

import lombok.Builder;
import lombok.Data;

@Data
public class ConvertAmountBySpendRuleRequest extends ConvertAmountRequest {

    private String spendRuleId;

    @Builder(builderMethodName = "convertAmountBySpendRuleRequestBuilder")
    public ConvertAmountBySpendRuleRequest(String fromCurrency, String toCurrency, String customerId, String amount,
            String spendRuleId) {
        super(fromCurrency, toCurrency, customerId, amount);
        this.spendRuleId = spendRuleId;
    }
}
