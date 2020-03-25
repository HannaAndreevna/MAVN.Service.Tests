package com.lykke.tests.api.service.mavnpropertyintegration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class PendingInvoicePaymentsResponseModel {

    private String locationCode;
    private String paymentType;
    private String source;
    private String status;
    private InvoiceDetailsModel[] invoiceDetail;

}
