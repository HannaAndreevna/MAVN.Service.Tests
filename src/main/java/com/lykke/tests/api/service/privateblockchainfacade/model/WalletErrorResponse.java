package com.lykke.tests.api.service.privateblockchainfacade.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class WalletErrorResponse {
    private static final String ID_FIELD_IS_REQUIRED = "The CustomerId field is required.";
    private static final String INPUT_WAS_NOT_VALID = "The input was not valid.";

    private String[] customerId;

    public String getIdFieldIsRequired() {
        return ID_FIELD_IS_REQUIRED;
    }

    public String getInputWasNotValid() {
        return INPUT_WAS_NOT_VALID;
    }
}
