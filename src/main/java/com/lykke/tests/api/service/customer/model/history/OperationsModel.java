package com.lykke.tests.api.service.customer.model.history;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.lykke.api.testing.annotations.PublicApi;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@PublicApi
public class OperationsModel {

    private String type;
    private String timestamp;
    private String amount;
    private String actionRule;
    private String otherSideCustomerEmail;
    private String partnerName;
}
