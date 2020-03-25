package com.lykke.tests.api.service.campaigns.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.api.base.model.ModelErrors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(value = "ModelErrors", ignoreUnknown = true)
public class BonusEngineErrorResponseModel {
    private BonusEngineErrorCode errorCode;
    private String errorMessage;
    // an additional field
    private ModelErrors modelErrors;

    public String getErrorCode() {
        return errorCode.getCode();
    }
}
