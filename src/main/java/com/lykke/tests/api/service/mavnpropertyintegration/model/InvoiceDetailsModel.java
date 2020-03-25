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
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvoiceDetailsModel {

    private String customerTrxId;
    private String trxDate;
    private String orgId;
    private String invoiceCurrencyCode;
    private String docSequenceValue;
    private String invoiceDescription;
    private int lineAmount;
    private int taxAmount;
    private int lineTotalAmount;
    private int invoiceAmount;
    private int invoiceAmountRemain;
    private String glDate;
    private String dueDate;
    private String type;
    private String customerTrxTypeName;
    private String partyId;
    private String customerAccountId;
    private String accountNumber;
    private String locationCode;
    private String invFlag;
    private String isLpf;
    private String serviceFlag;
}
