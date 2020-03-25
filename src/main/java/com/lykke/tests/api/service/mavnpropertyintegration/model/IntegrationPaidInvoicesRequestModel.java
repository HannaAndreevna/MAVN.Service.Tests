package com.lykke.tests.api.service.mavnpropertyintegration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
public class IntegrationPaidInvoicesRequestModel {
    private String customerId;
    private String invoiceId;
    private int amount;
    private String currency;
}
