package com.lykke.tests.api.service.campaigns.model.burnrules;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationErrorBurnRuleResponse {

    private String errorMessage;
    private ModelErrorModel modelErrors;

    public ValidationErrorBurnRuleResponse() {
        modelErrors = new ModelErrorModel();
    }
}
