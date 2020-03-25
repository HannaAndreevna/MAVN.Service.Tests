package com.lykke.tests.api.service.eligibilityengine.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ConvertAmountResponse extends EligibilityEngineErrorResponseModel {

    private String amount;
    private String currencyCode;
    private String usedRate;
    private ConversionSource conversionSource;

    public ConvertAmountResponse(EligibilityEngineError errorCode, String errorMessage, String amount,
            String currencyCode, String usedRate, ConversionSource conversionSource) {
        super(errorCode, errorMessage);
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.usedRate = usedRate;
        this.conversionSource = conversionSource;
    }
}
