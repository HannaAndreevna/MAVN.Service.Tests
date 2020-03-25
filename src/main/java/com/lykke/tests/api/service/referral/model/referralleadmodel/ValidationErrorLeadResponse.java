package com.lykke.tests.api.service.referral.model.referralleadmodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationErrorLeadResponse {

    private String errorMessage;
    private ModelErrorModel modelErrors;
    private String errorCode;

    public ValidationErrorLeadResponse() {
        modelErrors = new ModelErrorModel();
    }
}
