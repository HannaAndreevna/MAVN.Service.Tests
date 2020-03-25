package com.lykke.tests.api.service.operationshistory.model.TokensStatistics;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class ValidationErrorTokensResponse {

    private String errorMessage;
    private ModelErrorModel modelErrors;

    public ValidationErrorTokensResponse() {
        modelErrors = new ModelErrorModel();
    }
}
