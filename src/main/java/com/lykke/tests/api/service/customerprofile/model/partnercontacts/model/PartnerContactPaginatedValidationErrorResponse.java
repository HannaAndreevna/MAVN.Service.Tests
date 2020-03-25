package com.lykke.tests.api.service.customerprofile.model.partnercontacts.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(UpperCamelCaseStrategy.class)
public class PartnerContactPaginatedValidationErrorResponse {

    private String errorMessage;
    private PaginationModelErrorsModel modelErrors;

    public PartnerContactPaginatedValidationErrorResponse() {
        modelErrors = new PaginationModelErrorsModel();
    }
}
