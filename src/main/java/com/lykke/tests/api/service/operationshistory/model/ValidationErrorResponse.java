package com.lykke.tests.api.service.operationshistory.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class ValidationErrorResponse {

    private String errorMessage;
    private ModelErrorModel modelErrors;

    public ValidationErrorResponse() {
        modelErrors = new ModelErrorModel();
    }
}
