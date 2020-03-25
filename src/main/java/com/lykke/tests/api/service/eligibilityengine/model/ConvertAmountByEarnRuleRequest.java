package com.lykke.tests.api.service.eligibilityengine.model;

import lombok.Builder;
import lombok.Data;

@Data
public class ConvertAmountByEarnRuleRequest extends ConvertAmountRequest {

    private String earnRuleId;

    @Builder(builderMethodName = "convertAmountByEarnRuleRequestBuilder")
    public ConvertAmountByEarnRuleRequest(String fromCurrency, String toCurrency, String customerId, String amount,
            String earnRuleId) {
        super(fromCurrency, toCurrency, customerId, amount);
        this.earnRuleId = earnRuleId;
    }
}
