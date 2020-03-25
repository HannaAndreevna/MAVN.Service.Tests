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
public class CurrencyRateByConditionResponse extends CurrencyRateResponse {

    private String conditionId;

    @Builder(builderMethodName = "currencyRateByConditionResponseBuilder")
    public CurrencyRateByConditionResponse(EligibilityEngineError errorCode, String errorMessage, String rate,
            ConversionSource conversionSource, String conditionId) {
        super(errorCode, errorMessage, rate, conversionSource);
        this.conditionId = conditionId;
    }
}