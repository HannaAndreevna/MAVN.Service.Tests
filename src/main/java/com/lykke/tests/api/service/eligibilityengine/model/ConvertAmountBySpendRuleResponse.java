package com.lykke.tests.api.service.eligibilityengine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConvertAmountBySpendRuleResponse extends ConvertAmountResponse {

    private String spendRuleId;

    @Builder(builderMethodName = "convertAmountBySpendRuleResponseBuilder")
    public ConvertAmountBySpendRuleResponse(EligibilityEngineError errorCode, String errorMessage, String amount,
            String currencyCode, String usedRate, ConversionSource conversionSource, String spendRuleId) {
        super(errorCode, errorMessage, amount, currencyCode, usedRate, conversionSource);
        this.spendRuleId = spendRuleId;
    }
}
