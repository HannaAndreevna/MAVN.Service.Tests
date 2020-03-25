package com.lykke.tests.api.service.customer.model.history;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(LowerCaseStrategy.class)
public class OperationHistoryErrorResponseModel {

    private String error;
    private String message;
}
