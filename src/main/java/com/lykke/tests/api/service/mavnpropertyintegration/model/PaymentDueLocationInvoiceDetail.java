package com.lykke.tests.api.service.mavnpropertyintegration.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy.UpperCamelCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@JsonNaming(UpperCamelCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDueLocationInvoiceDetail {

    private String customerTrxId;
    private Date trxDate;
    private String orgId;
    private String invoiceCurrencyCode;
    private String docSequenceValue;
    private String invoiceDescription;
    private float lineAmount;
    private float taxAmount;
    private float lineTotalAmount;
    private float invoiceAmount;
    private float invoiceAmountRemain;
    private Date glDate;
    private Date dueDate;
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
