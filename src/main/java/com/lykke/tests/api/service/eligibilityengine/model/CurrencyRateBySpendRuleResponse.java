package com.lykke.tests.api.service.eligibilityengine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyRateBySpendRuleResponse extends CurrencyRateResponse {

    private String spendRuleId;

    @Builder(builderMethodName = "currencyRateBySpendRuleResponseBuilder")
    public CurrencyRateBySpendRuleResponse(EligibilityEngineError errorCode, String errorMessage, String rate,
            ConversionSource conversionSource, String spendRuleId) {
        super(errorCode, errorMessage, rate, conversionSource);
        this.spendRuleId = spendRuleId;
    }
}
