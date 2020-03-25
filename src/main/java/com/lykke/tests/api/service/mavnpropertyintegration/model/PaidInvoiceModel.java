package com.lykke.tests.api.service.mavnpropertyintegration.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonNaming(UpperCamelCaseStrategy.class)
public class PaidInvoiceModel {
    private String customerId;
    private String invoiceId;
    private int amount;
    private String currency;
    private String responseStatus;
    private String responseErrorCode;
    private String responsePaymentId;
}
