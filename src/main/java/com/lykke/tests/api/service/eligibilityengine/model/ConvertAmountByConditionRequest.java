package com.lykke.tests.api.service.eligibilityengine.model;

import lombok.Builder;
import lombok.Data;

@Data
public class ConvertAmountByConditionRequest extends ConvertAmountRequest {

    private String conditionId;

    @Builder(builderMethodName = "convertAmountByConditionRequestBuilder")
    public ConvertAmountByConditionRequest(String fromCurrency, String toCurrency, String customerId, String amount,
            String conditionId) {
        super(fromCurrency, toCurrency, customerId, amount);
        this.conditionId = conditionId;
    }
}
