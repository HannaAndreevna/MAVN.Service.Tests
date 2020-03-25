package com.lykke.tests.api.service.eligibilityengine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyRateResponse extends EligibilityEngineErrorResponseModel {

    private String rate;
    private ConversionSource conversionSource;

    public CurrencyRateResponse(EligibilityEngineError errorCode, String errorMessage, String rate,
            ConversionSource conversionSource) {
        super(errorCode, errorMessage);
        this.rate = rate;
        this.conversionSource = conversionSource;
    }
}
