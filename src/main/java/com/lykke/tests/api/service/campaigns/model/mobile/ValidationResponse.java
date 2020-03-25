package com.lykke.tests.api.service.campaigns.model.mobile;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class ValidationResponse {

    private String errorMessage;
    private ModelErrorModel modelErrors;

    public ValidationResponse() {
        modelErrors = new ModelErrorModel();
    }

}
